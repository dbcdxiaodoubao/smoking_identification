import cv2
import numpy as np
import os
import sys
import base64
from flask import Flask, request, jsonify
from ultralytics import YOLO

# ===================== Flask 服务 =====================
app = Flask(__name__)

sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))
import ultralytics.nn.tasks as nn_tasks
import torch.nn as nn
nn_tasks.LCSA = nn.Identity    # P2 裸架构，注意力占位跳过
nn_tasks.MSFFA = nn.Identity

try:
    model = YOLO("../smoke_detection/gpu_final_p2/weights/best.pt")
    print("[INFO] 模型: YOLOv8s + P2 + 清洁数据集")
except Exception as e:
    print(f"YOLO模型加载失败：{e}")
    model = None


def slice_detect(img, slice_size=640, overlap=0.2):
    """
    SAHI 切片检测：对大图切块，分别推理，合并结果
    """
    h, w = img.shape[:2]

    # 小图不需要切片
    if h <= slice_size and w <= slice_size:
        results = model(img, conf=0.15, iou=0.45, verbose=False)
        dets = []
        for r in results:
            for box in r.boxes:
                xyxy = box.xyxy[0].cpu().numpy()
                dets.append({
                    "x1": float(xyxy[0]), "y1": float(xyxy[1]),
                    "x2": float(xyxy[2]), "y2": float(xyxy[3]),
                    "conf": float(box.conf.item()),
                    "cls": int(box.cls.item())
                })
        return dets

    stride = int(slice_size * (1 - overlap))
    all_dets = []

    for y0 in range(0, h, stride):
        for x0 in range(0, w, stride):
            x1 = min(x0 + slice_size, w)
            y1 = min(y0 + slice_size, h)
            x0 = max(0, x1 - slice_size)
            y0 = max(0, y1 - slice_size)

            tile = img[y0:y1, x0:x1]
            results = model(tile, conf=0.15, iou=0.45, verbose=False)

            for r in results:
                for box in r.boxes:
                    xyxy = box.xyxy[0].cpu().numpy()
                    all_dets.append({
                        "x1": float(xyxy[0]) + x0, "y1": float(xyxy[1]) + y0,
                        "x2": float(xyxy[2]) + x0, "y2": float(xyxy[3]) + y0,
                        "conf": float(box.conf.item()),
                        "cls": int(box.cls.item())
                    })

    # NMS 合并重叠框
    if not all_dets:
        return []

    boxes_arr = np.array([[d["x1"], d["y1"], d["x2"], d["y2"]] for d in all_dets])
    scores = np.array([d["conf"] for d in all_dets])
    keep = cv2.dnn.NMSBoxes(boxes_arr.tolist(), scores.tolist(), 0.15, 0.45)

    merged = []
    if len(keep) > 0:
        for i in keep.flatten():
            merged.append(all_dets[i])
    return merged


@app.route('/detect', methods=['POST'])
def detect():
    """烟雾检测接口 — SAHI 切片增强"""
    if model is None:
        return jsonify({"error": "YOLO模型未加载成功"}), 500

    try:
        data = request.json
        img_data = base64.b64decode(data['image'])
        nparr = np.frombuffer(img_data, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        detections_raw = slice_detect(img, slice_size=640, overlap=0.2)

        detections = []
        for d in detections_raw:
            detections.append({
                "class_name": model.names[d["cls"]],
                "confidence": d["conf"],
                "x": d["x1"],
                "y": d["y1"],
                "w": d["x2"] - d["x1"],
                "h": d["y2"] - d["y1"]
            })

        return jsonify(detections)
    except Exception as e:
        return jsonify({"error": f"检测失败：{str(e)}"}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
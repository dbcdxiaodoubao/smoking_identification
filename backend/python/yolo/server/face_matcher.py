"""
高精度人脸匹配模块
先检测+裁剪人脸，再比较人脸区域，避免背景干扰
"""

import os

os.environ["DEEPFACE_HOME"] = os.path.join(
    os.path.dirname(os.path.abspath(__file__)), "..", "deepface_models")

import numpy as np
import cv2
import traceback
from deepface import DeepFace

SUPPORTED_FORMATS = ('.jpg', '.jpeg', '.png', '.bmp', '.webp')
DETECTOR_ORDER = ["retinaface", "mtcnn", "opencv", "ssd"]


def cv_imread_chinese(img_path: str):
    try:
        stream = np.fromfile(img_path, dtype=np.uint8)
        return cv2.imdecode(stream, cv2.IMREAD_COLOR)
    except Exception:
        return None


def extract_face(img, detector_backend: str = "retinaface"):
    """
    从图片中提取人脸，返回 (人脸数组, 检测器名称) 或 (None, None)
    依次尝试多个检测器直到成功
    """
    backends = [detector_backend] + [b for b in DETECTOR_ORDER if b != detector_backend]

    for backend in backends:
        try:
            faces = DeepFace.extract_faces(
                img_path=img,
                detector_backend=backend,
                enforce_detection=True,
                align=True,
                expand_percentage=20
            )
            if faces and len(faces) > 0:
                # 取最大的人脸
                best = max(faces, key=lambda f: f["facial_area"]["w"] * f["facial_area"]["h"])
                face_img = (best["face"] * 255).astype(np.uint8)
                if face_img.shape[2] == 3:
                    face_img = cv2.cvtColor(face_img, cv2.COLOR_RGB2BGR)
                return face_img, backend
        except Exception:
            continue

    return None, None


def verify_faces(img1_path: str, img2_path: str,
                 model_name: str = "Facenet512",
                 detector_backend: str = "retinaface",
                 silent: bool = False) -> dict:

    if not os.path.exists(img1_path):
        return {"verified": False, "distance": 999.0, "error": f"图片不存在: {img1_path}"}
    if not os.path.exists(img2_path):
        return {"verified": False, "distance": 999.0, "error": f"图片不存在: {img2_path}"}

    img1 = cv_imread_chinese(img1_path)
    img2 = cv_imread_chinese(img2_path)
    if img1 is None or img2 is None:
        return {"verified": False, "distance": 999.0,
                "error": f"无法读取: {img1_path if img1 is None else img2_path}"}

    # 先提取人脸区域
    face1, det1 = extract_face(img1, detector_backend)
    face2, det2 = extract_face(img2, detector_backend)

    if face1 is None:
        if not silent:
            print(f"  [NOFACE] input image - no face detected")
        return {"verified": False, "distance": 999.0,
                "error": "Input image: no face detected", "detector": detector_backend}

    if face2 is None:
        if not silent:
            print(f"  [NOFACE] {os.path.basename(img2_path)} - no face detected")
        return {"verified": False, "distance": 999.0,
                "error": "Target image: no face detected", "detector": detector_backend}

    if not silent:
        print(f"  [DETECT] input: {face1.shape[1]}x{face1.shape[0]} ({det1})  "
              f"target: {face2.shape[1]}x{face2.shape[0]} ({det2})")

    # 用提取的人脸区域做验证
    try:
        result = DeepFace.verify(
            img1_path=face1,
            img2_path=face2,
            model_name=model_name,
            detector_backend="skip",  # 跳过检测，直接对输入做嵌入
            distance_metric="cosine",
            enforce_detection=False,
            align=False,
            silent=True
        )

        distance = round(float(result.get("distance", 999.0)), 4)
        threshold = round(float(result.get("threshold", 0.0)), 4)
        verified = bool(result.get("verified", False))

        if not silent:
            status = "MATCH" if verified else ("CLOSE" if distance < 0.6 else "DIFF")
            print(f"  [{status}] {os.path.basename(img2_path):30s}  "
                  f"distance={distance:<8.4f}  threshold={threshold}")

        return {"verified": verified, "distance": distance,
                "threshold": threshold, "error": None}

    except Exception as e:
        if not silent:
            print(f"  [ERR]  {os.path.basename(img2_path):30s}  {str(e)[:100]}")
            traceback.print_exc()
        return {"verified": False, "distance": 999.0, "error": str(e)}


def find_best_match(input_path: str, target_dir: str,
                    model_name: str = "Facenet512",
                    detector_backend: str = "retinaface",
                    min_confidence: float = 0.30) -> dict:
    if not os.path.exists(input_path):
        return {"error": f"输入图片不存在: {input_path}",
                "most_similar_name": "", "similarity": 0.0, "all_results": []}
    if not os.path.isdir(target_dir):
        return {"error": f"目标目录不存在: {target_dir}",
                "most_similar_name": "", "similarity": 0.0, "all_results": []}

    print("\n" + "=" * 60)
    print(f"[FACE MATCH] Input: {os.path.basename(input_path)}")
    print(f"[FACE MATCH] Model: {model_name}, Detector: {detector_backend}")
    print("-" * 60)

    best_match = ""
    best_distance = 999.0
    all_results = []

    for filename in os.listdir(target_dir):
        if not filename.lower().endswith(SUPPORTED_FORMATS):
            continue
        target_path = os.path.join(target_dir, filename)
        if os.path.abspath(target_path) == os.path.abspath(input_path):
            continue

        result = verify_faces(input_path, target_path,
                              model_name=model_name,
                              detector_backend=detector_backend)

        distance = result["distance"]
        verified = distance < min_confidence
        similarity = round(max(0.0, (1.0 - distance)) * 100.0, 2)

        if distance < 0.25:
            level = "极高"
        elif distance < 0.35:
            level = "较高"
        elif distance < 0.50:
            level = "中等（疑似）"
        elif distance < 0.70:
            level = "较低（待确认）"
        else:
            level = "极低"

        all_results.append({
            "filename": filename,
            "distance": distance,
            "similarity": similarity,
            "confidence_level": level,
            "verified": verified
        })

        if distance < best_distance:
            best_distance = distance
            best_match = filename

    all_results.sort(key=lambda x: x["distance"])

    if best_distance < 0.25:
        best_level = "极高"
    elif best_distance < 0.35:
        best_level = "较高"
    elif best_distance < 0.50:
        best_level = "中等（疑似）"
    elif best_distance < 0.70:
        best_level = "较低（待确认）"
    else:
        best_level = "极低"

    best_similarity = round(max(0.0, (1.0 - best_distance)) * 100.0, 2)
    verified = best_distance < min_confidence

    print("-" * 60)
    print(f"[RESULT] Best: {best_match or 'N/A'}  distance={best_distance:.4f}  "
          f"similarity={best_similarity}%  level={best_level}  verified={verified}")
    print("=" * 60 + "\n")

    if not best_match:
        return {"error": "目标目录中未找到可对比的图片",
                "most_similar_name": "", "similarity": 0.0,
                "confidence_level": "极低", "all_results": all_results}

    return {
        "error": None,
        "most_similar_name": best_match,
        "distance": round(best_distance, 4),
        "similarity": best_similarity,
        "confidence_level": best_level,
        "verified": verified,
        "all_results": all_results
    }

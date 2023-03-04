from ultralytics import YOLO

# 加载模型
model = YOLO("yolov8n.yaml")  # 从头开始构建新模型
model = YOLO("yolov8n.pt")  # 加载预训练模型（推荐用于训练）
# Use the model
results = model.train(data="coco.yaml", epochs=3)  # 训练模型
results = model.val()  # 在验证集上评估模型性能
results = model("https://ultralytics.com/images/bus.jpg")  # 预测图像
success = model.export(format="onnx")  # 将模型导出为 ONNX 格式
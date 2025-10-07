import urllib.request
import os

# 创建lib目录（如果不存在）
lib_dir = "lib"
if not os.path.exists(lib_dir):
    os.makedirs(lib_dir)

# 下载Vosk JAR文件
url = "https://github.com/alphacep/vosk-api/releases/download/v0.3.47/vosk-0.3.47.jar"
filename = os.path.join(lib_dir, "vosk-0.3.47.jar")

print("开始下载Vosk JAR文件...")
urllib.request.urlretrieve(url, filename)
print("Vosk JAR文件下载完成!")
# Phần hướng dẫn của visual studio code về Java 

## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

# Phần chính tui giới thiệu
# Các sử dụng git:
- Tải git về [ở đây](https://github.com/git-for-windows/git/releases/download/v2.45.1.windows.1/Git-2.45.1-64-bit.exe)
- Tạo một thư mục ở đâu đó trong máy. Bấm chuột phải vào thư mục đó chọn **Open gitbash here**
- Clone project về: **git clone https://github.com/nupniichan/CoVua.git**
- Tạo nhánh mới (đừng sử dụng nhánh main): **git branch (tên nhánh)**
- Chuyển sang nhánh đó để làm việc: **git checkout (tên nhánh)**
- Sau đó làm việc trên nhánh đó rồi commit code lên (sử dụng từng lệnh đừng copy toàn bộ): **git add .** / **git commit -m "tin nhắn (ví dụ: sửa bug, thêm tính năng ,...)"** / **git push**
- Rồi tạo pull request [ở đây](https://github.com/nupniichan/QuanLyTiemNet/pulls) để tui kiểm tra code rồi add vào nhánh chính. **Nếu được thì thêm cho tui 1 vài description để tui check cho dễ cũng được**. Xong bấm nút xác nhận rồi chờ tui reply thôi

# Một vài lưu ý
- Ngay trước khi mở Visual Studio Code (Hoặc IDE nào đó mà các ông xài) lên thì nhớ sử dụng **git pull origin main** để lấy code mới nhất được tui update trên nhánh main về làm để tránh bị lỗi **HOẶC** sử dụng **git checkout main** rồi **git pull** khi nào nó hiện ra giống như thế này là được. <br />
![image](https://github.com/nupniichan/QuanLyTiemNet/assets/87707214/8e9601f3-e808-46db-aa74-9a034cba48f9)
- Kiểm tra trạng thái nhánh trước khi làm việc: **git status** <br />
![image](https://github.com/nupniichan/QuanLyTiemNet/assets/87707214/22f8e1f1-59d3-4392-8967-787d45719dbe)
- Nhớ chuyển sang nhánh mấy ông đã tạo để làm **Đừng làm trên nhánh main**
- Lúc sử dụng **git commit -m "tin nhắn"** thì ở chỗ tin nhắn mấy ông liệt kê càng chi tiết càng tốt nha

# Các công việc cần làm
- Di chuyển quân cờ
- Test chức năng ăn quân cờ 
- Hiển thị điểm trong game khi ăn quân cờ của đối thủ
- Tạo AI để chơi với máy
- Chức năng của Cài đặt
- Chức năng tái đấu

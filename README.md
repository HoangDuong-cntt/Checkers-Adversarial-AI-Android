# Checkers-Adversarial-AI-Android
Checkers Game Adversarial Artificial Intelligence Android Application

Ứng dụng trò chơi cờ Đam (Checkers) có tích hợp AI đối kháng và Firebase Realtime Database.
-

TỔNG QUAN:
-
 Ứng dụng là một trò chơi cờ Đam đơn giản với 2 chế độ là chơi người đấu với nguời và người đấu với Máy (AI). Với bộ luật chuẩn quốc tế thì ứng dụng bảo đảm sự công bằng trong trò chơi, ngoài ra người chơi có thể điều chỉnh dữ liệu trên Cloud ( Fỉrebase Realtime) về làm thay đổi toàn bộ tính cách và chiến thuật của AI ngay trong trận đấu, giúp AI thông minh hơn và tăng độ khó cho trò chơi.

CẤU TRÚC:
-
Dự án được cấu trúc theo mô hình phân lớp rõ ràng nhằm tách biệt giao diện, dữ liệu và logic xử lý:

                 Checkers-Adversarial-AI-Android    
                               └── checkersgame 
                                    ├── ai 
                                    ├── firebase 
                                    ├── logic 
                                    ├── models  
                                    ├── utils
                                    └── ui

- ai: Thuật toán Minimax kết hợp Cắt tỉa hai nhánh Alpha và Beta để nhanh chóng sàng lọc và trả về nước đi tối ưu nhất.
- firebase: Quản lý luồng kết nối cơ sở dữ liệu Firebase Realtime Database.
- logic: Chứa các lớp xử lý quy tắc cờ Đam độc lập hoàn toàn với thành phần giao diện.
- models: Chứa lớp định nghĩa cấu trúc dữ liệu cốt lõi của bàn cờ.
- utils: Lưu trữ tập trung toàn bộ các hằng số cố định dùng chung trong ứng dụng như kích thước bàn cờ, mã màu hex của ô cờ, mã số định danh quân cờ.
- ui: (Interface User) hiển thị trực quan đồ họa và tiếp nhận tương tác chạm (Touch Event) từ người sử dụng.

ỨNG DỤNG ANDROID:
-
- Giao diện ban đầu của trò chơi.
<img width="398" height="873" alt="NewInterface" src="https://github.com/user-attachments/assets/b2e3432e-bcc4-4002-bacd-6a4deee6838c" />

- Chế độ người (quân trắng) đấu với AI (quân đen), mặc định là quân trắng đi trước. Dựa theo luật chơi thì có BẮT BUỘC ăn quân, nên khi có quân cờ buộc phải ăn thì phải đi nước cờ ăn quân đó.
<img width="392" height="870" alt="AI" src="https://github.com/user-attachments/assets/5291363c-f92f-4f65-a7e9-d8e4430e90ff" />

- Chế độ người đấu với người: có thể di chuyển cả quân trắng và quân đen, như 2 người chơi cờ với nhau bình thường.
<img width="412" height="875" alt="PVP" src="https://github.com/user-attachments/assets/4c9bac78-d3d0-4bd9-8c3c-98eb53f4eea0" />

- Phong Vua sẽ có chấm tròn ở giữa, và sau khi 1 trong 2 giành chiến thắng sẽ hiện Thông báo "Chiến Thắng" hoặc khi bấm vào Chơi lại ván mới cũng  sẽ hiện Thông báo.
<img width="404" height="876" alt="win" src="https://github.com/user-attachments/assets/de62d889-e8cf-472a-955e-800e8215f6f4" />

- Tính năng lưu lại Lịch Sử Trận Đấu.
<img width="399" height="875" alt="Luu thanhtich" src="https://github.com/user-attachments/assets/12358e7c-edd7-47d4-926a-c44393027dcf" />

- Xem lại Lịch Sử Các Trận Đấu với hiển thị chế độ, thời gian chơi, thành tích Thắng/Thua.
<img width="394" height="871" alt="UpdateResult" src="https://github.com/user-attachments/assets/96d1fa6e-6255-4bb9-9c2e-b0c5bcea2dde" />

- Để người chơi dễ dàng tiếp cận trò chơi hơn, trò chơi sẽ cập nhật thêm tính năng Hướng dẫn chơi (luật chơi), khi bấm vào sẽ hiện ra chi tiết cách trò chơi hoạt động.
<img width="394" height="867" alt="Rules" src="https://github.com/user-attachments/assets/8cdee72e-c9b6-45b0-9627-1803a66cc43a" />


FIREBASE REALTIME DATABASE:
-
Việc đưa trọng số lên Firebase là để triển khai cơ chế Điều chỉnh chiến thuật AI từ xa không cần cập nhật ứng dụng:
- Nếu nhận thấy AI mặc định (100/250) quá hiếu chiến và dễ bị người chơi bẫy, chỉ cần lên trang Web Console của Firebase hạ thông số xuống thành 10/20 (hoặc bất cứ thông số nào).
- Ngay lập tức, tất cả điện thoại của người dùng đang bật game sẽ tự động cập nhật hệ số mới thông qua luồng lắng nghe realtime, làm thay đổi toàn bộ tính cách và chiến thuật của AI ngay trong trận đấu.
<img width="534" height="192" alt="Screenshot 2026-05-20 at 18 16 33" src="https://github.com/user-attachments/assets/2b3992fd-7773-42b8-850e-97f3a862a3c8" />

<img width="1081" height="67" alt="Screenshot 2026-05-20 at 18 15 45" src="https://github.com/user-attachments/assets/a4b34ab1-80da-43f0-9b3f-6cb0c2a2d23b" />

-  Sau khi thành tích mỗi trận đấu được lưu lại thì trên Firebase Realtime cũng sẽ có update.
<img width="498" height="489" alt="Screenshot 2026-05-25 at 19 08 21" src="https://github.com/user-attachments/assets/3a3936c3-9530-45f2-8807-e08b6614dede" />













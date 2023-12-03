

// checkout

// Lấy các phần tử cần kiểm tra
// const orderButton = document.querySelector('a[data-target="#myModal"]');

// const phoneField = document.querySelector('input[ng-model="order1.phone"]');
// const provinceField = document.getElementById('province');
// const districtField = document.getElementById('district');
// const serviceField = document.getElementById('service');
// const wardField = document.getElementById('ward');
// const addressField = document.getElementById('result');




// // Function kiểm tra thông tin nhập liệu
// function checkInputs() {
//     const username = usernameField.textContent.trim();
//     const phone = phoneField.value.trim();
//     const province = provinceField.value.trim();
//     const district = districtField.value.trim();
//     const service = serviceField.value.trim();
//     const ward = wardField.value.trim();
//     const address = addressField.value.trim();

//     // Kiểm tra nếu tất cả trường đã được điền đầy đủ thì kích hoạt nút thanh toán
//     if (username !== '' && phone !== '' && province !== '' && district !== '' && service !== '' && ward !== '' && address !== '' && isValidPhoneNumber(phone)) {
//         checkInputs();
//         orderButton.classList.remove('disabled');
//         orderButton.removeAttribute('data-toggle');
//         orderButton.removeAttribute('data-target');
//     } else {
//         orderButton.classList.add('disabled');
//         orderButton.setAttribute('data-toggle', 'modal');
//         orderButton.setAttribute('data-target', '#myModal');

//     }
// }

// // Lắng nghe sự kiện khi nhập liệu thay đổi
// usernameField.addEventListener('input', checkInputs);
// phoneField.addEventListener('input', checkInputs);
// provinceField.addEventListener('change', checkInputs);
// districtField.addEventListener('change', checkInputs);
// serviceField.addEventListener('change', checkInputs);
// wardField.addEventListener('change', checkInputs);
// addressField.addEventListener('input', checkInputs);


// // Kiểm tra trạng thái khi trang được tải
// checkInputs();
// // // Hàm kiểm tra định dạng số điện thoại
// function isValidPhoneNumber(phone) {
//     // Sử dụng biểu thức chính quy để kiểm tra định dạng số điện thoại (ví dụ: số điện thoại Việt Nam 10 hoặc 11 chữ số)
//     const phoneRegex = /^\d{10}$/;
//     return phoneRegex.test(phone);
// }



const app = angular.module("shopping-cart-app", []);
jQuery(document).ready(function ($) {
    $('.bar-menu').click(function (event) {
        $(this).next('.list-child').slideToggle(300);
    });
});
app.controller("shopping-cart-ctrl", function ($scope, $http) {


    // Kiểm tra đăng nhập
    function isLoggedIn() {
        return $("#username").text().trim() !== "";
    }
    $scope.cart = {
        items: [],
        getitem: {},
        getcomment: [],
        get_orderid: 0,
        prod: [],
        add(product_id) {
            if (isLoggedIn()) {
                $http.get(`/rest/products/${product_id}`).then(resp => {
                    const product = resp.data;

                    if (product.quantity <= 0) {
                        // Hiển thị thông báo sản phẩm đã hết hàng
                        themSanPham("error", "Sản phẩm đã hết hàng!");
                    } else {
                        $http.get(`/rest/carts`).then(resp => {
                            this.items = resp.data;
                            var item = this.items.find(item => item.product_id === product_id);

                            if (item) {
                                // Kiểm tra nếu số lượng sản phẩm trong kho vẫn còn thì tăng số lượng
                                if (item.quantity < product.quantity) {
                                    item.quantity++;
                                    this.saveToLocalStorage();
                                    themSanPham("success", "Đã thêm sản phẩm vào giỏ hàng!");
                                } else {
                                    // Hiển thị thông báo khi vượt quá số lượng trong kho
                                    themSanPham("error", "Số lượng sản phẩm trong kho không đủ!");
                                }
                            } else {
                                product.quantity = 1;
                                this.items.push(product);
                                this.saveToLocalStorage();
                                themSanPham("success", "Đã thêm sản phẩm vào giỏ hàng!");
                            }
                        });
                    }
                });
            } else {
                themSanPham("warning", "Vui lòng đăng nhập!");
            }
        },
        get_infoorderid(orderid) {
            this.get_orderid = orderid;

        }
        ,
        getinfComment(product_id) {
            $http.get(`/rest/comments/${product_id}`).then(resp => {
                this.getcomment = resp.data;
                $scope.isshowcomment = false;

            })
        }
        ,
        getinfoproducts(product_id) {
            var item = this.items.find(item => (item.product_id == product_id));
            $http.get(`/rest/products/${product_id}`).then(resp => {
                this.getitem = resp.data;

            })
        }
        ,
        maxquantity(product_id) {
            $http.get(`/rest/products/${product_id}`).then(resp => {
                this.prod = resp.data;
            })
            return this.prod.quantity;
        }
        ,
        remove(product_id) {
            // alert("Xóa sản phẩm thành công")
            var index = this.items.findIndex(item => item.product_id == product_id);
            this.items.splice(index, 1);
            this.saveToLocalStorage();
        }
        ,
        clear() {
            this.items = [];
            this.saveToLocalStorage();

        }
        ,
        amt_of(item) {
        },
        get count() {
            return this.items
                .map(item => item.quantity)
                .reduce((total, quantity) => total += quantity, 0);
        }
        ,
        get amount() {
            return this.items
                .map(item => item.quantity * item.unit_price - ((item.quantity * item.unit_price) * item.distcount / 100))
                .reduce((total, quantity) => total += quantity, 0);

        }
        ,
        get amount1() {
            return this.items
                .map(item => item.quantity * item.unit_price)
                .reduce((total, quantity) => total += quantity, 0);

        }
        ,
        get amount2() {
            return this.items
                .map(item => (item.quantity * item.unit_price) - (item.quantity * item.unit_price - ((item.quantity * item.unit_price) * item.distcount / 100)))
                .reduce((total, quantity) => total += quantity, 0);
        }
        ,
        saveToLocalStorage() {
            var json = JSON.stringify(angular.copy(this.items));
            localStorage.setItem("cart", json);
            $http.post(`/rest/carts`, JSON.stringify(this.items)).then(resp => {
            })
        }
        ,
        loadFromLocalStorage() {

            var json = localStorage.getItem("cart");
            this.items = json ? JSON.parse(json) : [];
        }
        ,
        initData() {
            $http.get(`/rest/carts`).then(resp => {
                this.items = resp.data;

            })
        }


    }

    // bắt lỗi thêm sản phảm vào giỏ hàng
    $scope.checkQuantity = function (item) {
        $http.get(`/rest/products/${item.product_id}`).then(resp => {
            const availableQuantity = resp.data.quantity; // Số lượng có sẵn trong cơ sở dữ liệu
            const requestedQuantity = item.quantity; // Số lượng người dùng muốn thêm vào giỏ hàng

            if (requestedQuantity <= 0) {
                themSanPham("error", "Số lượng không hợp lệ!");
                item.quantity = 1;
            } else if (requestedQuantity > availableQuantity) {
                themSanPham("error", "Sản phẩm không đủ số lượng!");
                item.quantity = availableQuantity; // Đặt lại số lượng về số lượng có sẵn trong cơ sở dữ liệu
            } else {
                // Cập nhật số lượng sản phẩm trong localStorage
                const cart = JSON.parse(localStorage.getItem('cart')) || [];
                const existingItemIndex = cart.findIndex(cartItem => cartItem.product_id === item.product_id);

                if (existingItemIndex !== -1) {
                    cart[existingItemIndex].quantity = requestedQuantity; // Cập nhật số lượng sản phẩm
                }

                localStorage.setItem('cart', JSON.stringify(cart));

            }
        });
    };
    // chặn nhập số âm  
    $scope.preventNegativeInput = function (event) {
        if (event.charCode !== 0 && (event.which < 48 || event.which > 57)) {
            event.preventDefault();
        }
    };



    $scope.generatePayment = function () {
        var tongtienthanhtoanElement = document.getElementById("total");
        // Lấy nội dung từ phần tử
        var tongtienthanhtoanText = tongtienthanhtoanElement.innerHTML;
        // Chuyển đổi chuỗi thành kiểu số
        // var total = parseFloat(tongtienthanhtoanText.replace('.', ''));
        var total = parseFloat(tongtienthanhtoanText.replace(/\./g, ''));
        console.log(total);
        var storedValue = sessionStorage.getItem('orderid');
        console.log(sessionStorage.getItem('orderid'))
        var params = {
            bankCode: "NCB",
            price: total,
            orderid: sessionStorage.getItem('orderid'),
        };
        console.log('Request Params:', params)
        $http.get(`/api/v1/pay`, { params: params })
            .then(function (response) {
                console.log('Response:', response);
                $scope.payment = response.data;
                window.location.href = $scope.payment;
            })
            .catch(function (error) {
                console.error('Error:', error);
            });
    };
    //Thanh toán vnpay	
    $scope.payment = function () {
        var phone = document.getElementById("phone").value;

        var tongtienthanhtoanElement = document.getElementById("total");
        // Lấy nội dung từ phần tử
        var tongtienthanhtoanText = tongtienthanhtoanElement.innerHTML;
        // Chuyển đổi chuỗi thành kiểu số
        // var total = parseFloat(tongtienthanhtoanText.replace('.', ''));
        var total = parseFloat(tongtienthanhtoanText.replace(/\./g, ''));
        console.log(total);


        $scope.order = {
            createDate: new Date(),
            phone: phone,
            status: 0,
            intent: 'Sale',
            method: 'Trả trước',
            currency: 'VND',
            address: localStorage.getItem("address"),
            price: total,
            account: {
                username: $("#username").text()
            },
            description: "Đã thanh toán",
            get orderDetails() {
                return $scope.cart.items.map(item => {
                    return {
                        product: { product_id: item.product_id },
                        price: item.unit_price,
                        quantity: item.quantity
                    }
                });
            },
            purchase() {
                var order = angular.copy(this);
                console.log(order);
                $http.post(`/rest/orders`, order).then(resp => {
                    console.log(resp.data);
                    console.log(resp.data.order_id);
                    sessionStorage.setItem('orderid', resp.data.order_id);
                    $scope.cart.clear();
                }).catch(error => {
                    alert("Đặt hàng thất bại");
                    console.log(error)
                })
            }
        }
       
    }


    $scope.thanhtoan = function () {
        $scope.payment();
        $scope.generatePayment();

    }

    // thanh toan thuong
    $scope.order1 = {
        createDate: new Date(),
        address: "",
        phone: "",
        status: 0,
        intent: 'Sale',
        method: 'Trả sau',
        currency: 'VND',
        description: 'Chưa thanh toán',

        account: { username: $("#username").text() },
        get orderDetails() {
            return $scope.cart.items.map(item => {
                return {
                    product: { product_id: item.product_id },
                    price: item.unit_price,
                    quantity: item.quantity
                }
            });
        },
        purchase() {

            var totalElement = document.getElementById("total");
            if (totalElement) {
                var totalValue = totalElement.innerText;
                var username = document.getElementById("username").innerText.trim();
                var phone = document.querySelector('input[ng-model="order1.phone"]').value.trim();
                var province = document.getElementById("province").value.trim();
                var district = document.getElementById("district").value.trim();
                var service = document.getElementById("service").value.trim();
                var ward = document.getElementById("ward").value.trim();
                var address = document.getElementById("result").value.trim();

                // Kiểm tra xem các trường thông tin có bị trống không
                if (username === '' || phone === '' || province === '' || district === '' || service === '' || ward === '' || address === '') {
                    themSanPham("error", "Vui lòng điền đầy đủ thông tin");
                    throw new Error("Vui lòng điền đầy đủ thông tin đặt hàng.");

                }
                var numericValue = parseFloat(totalValue.replace("Đ", "").replace(/\./g, "").replace(",", "."));
                // Gán giá trị lấy được vào thuộc tính price của order1
                this.price = numericValue;
            } else {
                console.log("Không tìm thấy phần tử có id là 'total'");
            }
            // Lấy giá trị từ trường địa chỉ
            var addressValue = document.getElementById("result").value;

            this.address = addressValue;
            var order = angular.copy(this);
            $http.post("/rest/orders", order).then(resp => {
                datHang("success", "Đặt hàng thành công!");
                $scope.cart.clear();
                if ($("#username").text()) {
                    $http.post(`/rest/carts/` + $("#username").text(), JSON.stringify(angular.copy(this.items))).then(resp => {
                    })
                }
                location.href = "/order/detail/" + resp.data.order_id;
            }).catch(error => {
                datHang("error", "Dặt hàng thất bại!");
                console.log(error)
            })
        },
        purchase1() {
            var order = angular.copy(this);
            $http.post("/rest/orders", order).then(resp => {
            }).catch(error => {
                datHang("error", "Dặt hàng thất bại!");
                console.log(error)
            })
        },
        purchase2() {
            $scope.cart.clear();
        }
    }



    //
    $scope.pager = {
        page: 0,
        size: 10,
        get items() {
            var start = this.page * this.size;
            return $scope.cart.items.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1 * $scope.cart.items.length / this.size);
        },
        first() {
            this.page = 0;
        },
        prev() {
            this.page--;
            if (this.page < 0) {
                this.last();
            }
        },
        next() {
            this.page++;
            if (this.page >= this.count) {
                this.first();
            }
        },
        last() {
            this.page = this.count - 1;
        }
    }

    $scope.facolist = [];
    $scope.formfavo = {
        favorite_date: new Date(),
        account: { username: $("#username").text() },
        product: { product_id: 0 }
    };


    $scope.isShow = false;

    $scope.Addfavorite = function (idindex, isShow) {
        $scope.formfavo.product.product_id = idindex;
        var favo = angular.copy($scope.formfavo);
        $http.post(`/rest/favorites`, favo).then(resp => {
            resp.data,
                favorite_date = new Date(resp.data.favorite_date);
            $scope.facolist.push(resp.data);
            themYeuThich("success", "Đã thêm sản phẩm vào danh sách yêu thích");
            $scope.isShow = isShow;
        }).catch(error => {
            themYeuThich("warning", "Bạn cần đăng nhập để sử dụng chức năng này");
            console.log("Error", error);
        })
    }


    // xóa yêu thích
    $scope.deleteFavoriteUser = function (idindex, isShow) {
        $scope.formfavo.product.product_id = idindex;
        $http.delete(`/rest/favorites/${$scope.formfavo.product.product_id}` + `/${$scope.formfavo.account.username}`).then(resp => {

            themYeuThich("success", "Đã xoá sản phẩm ra khỏi danh sách yêu thích");
            $scope.isShow = isShow;
        }).catch(error => {
            themYeuThich("success", "Xoá sản phẩm ra khỏi danh sách yêu thích thất bại");
            console.log("Error", error);
        })
    }

    // bình luận
    $scope.isUsername = $("#isusername").text();
    $scope.commentList = [];
    $scope.commentedit = {};
    $scope.commentform = {
        comment_date: new Date(),
        account: { username: $("#username").text() },
        product: { product_id: 0 },
        comment_Content: "",
    };

    $scope.isshowcomment = false;
    $scope.isshowcomment = function () {
        $scope.isshowcomment = false;
    }

    $scope.AddComment = function (id) {
        if (!$scope.commentform.comment_Content) {
            themSanPham("error", "Vui lòng nhập nội dung!");
            return;
        }

        $scope.commentform.product.product_id = id;
        var comm = angular.copy($scope.commentform);
        $http.post(`/rest/comments`, comm).then(resp => {
            resp.data,
                comment_date = new Date(resp.data.comment_date);
            $scope.commentList.push(resp.data);
            $scope.commentform.comment_Content = "";
            $scope.cart.getinfComment(id);
        }).catch(error => {
            themYeuThich("warning", "Bạn cần đăng nhập để sử dụng chức năng này");
            console.log("Error", error);
        })
    };


    $scope.deleteComment = function (cmt) {
        var index = $scope.cart.getcomment.findIndex(cmts => cmts.comment_id == cmt.comment_id);
        $scope.cart.getcomment.splice(index, 1);
        $scope.cart.saveToLocalStorage();
        $http.delete(`/rest/comments/${cmt.comment_id}`).then(resp => {
        }).catch(error => {
            alert("Xoá bình luận thất bại");
            console.log("Error", error);
        })
    };

    $scope.UpdateComment = function () {
        if (!$scope.commentedit || !$scope.commentedit.comment_id || !$scope.commentedit.comment_Content) {
            themSanPham("error", "Không được trống bình luận");
            return;
        }
        var comm = angular.copy($scope.commentedit);
        $http.put(`/rest/comments/${comm.comment_id}`, comm).then(resp => {
            var index = $scope.cart.getcomment.findIndex(cmts => cmts.comment_id == comm.comment_id);
            ;
            $scope.cart.getcomment[index] = comm;
            $scope.isshowcomment = false;
        }).catch(error => {
            alert("Chỉnh sửa bình luận thất bại ");
            console.log("Error", error);
        })
    };

    $scope.editcomment = function (cmt) {
        $scope.commentedit = angular.copy(cmt);
        $scope.isshowcomment = true;
    }

}
);



$(document).ready(function () {
    var appElement = document.querySelector('[ng-app="shopping-cart-app"]');
    var $scope = angular.element(appElement).scope();

    // Gọi hàm loadFromLocalStorage trong phạm vi của $scope
    $scope.cart.initData();
});

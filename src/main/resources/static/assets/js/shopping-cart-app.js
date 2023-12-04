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

        //
        saveToLocalStorage: function () {
            var cartData = {
                username: $("#username").text(), // Lưu thông tin tài khoản người dùng
                items: this.items // Lưu dữ liệu giỏ hàng
            };
            localStorage.setItem("cartData", JSON.stringify(cartData));
        },

        loadFromLocalStorage: function () {
            var savedCartData = localStorage.getItem("cartData");
            if (savedCartData) {
                var cartData = JSON.parse(savedCartData);
                if (cartData.username === $("#username").text()) {
                    this.items = cartData.items; // Nạp dữ liệu giỏ hàng từ Local Storage
                }
            }
        },

        clearLocalStorage: function () {
            localStorage.removeItem("cartData"); // Xóa dữ liệu giỏ hàng khi đăng xuất
            this.items = []; // Xóa dữ liệu giỏ hàng trong ứng dụng
        },
        //



        add(product_id) {

            if (isLoggedIn()) {
                $http.get(`/rest/carts`).then(resp => {
                    this.items = resp.data;
                    var item = this.items.find(item => (item.product_id == product_id));

                    if (item) {
                        item.quantity++;
                        this.saveToLocalStorage();
                        themSanPham("success", "Đã thêm sản phẩm vào giỏ hàng!");

                    }
                    else {
                        // $http.get(`/rest/products/${product_id}`).then(resp => {
                        $http.get('/rest/products/' + encodeURIComponent(product_id)).then(resp => {
                            resp.data.quantity = 1;
                            this.items.push(resp.data);
                            this.saveToLocalStorage();
                            themSanPham("success", "Đã thêm sản phẩm vào giỏ hàng!");
                        });
                    }
                });
            } else {
                themSanPham("warning", "Vui lòng đăng nhập  !");
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


                // Thêm trường checked cho mỗi sản phẩm
                // this.items.forEach(item => {
                //     item.checked = false; // Mặc định không chọn
                // });
            })
        }


    }

    // // Load dữ liệu giỏ hàng từ Local Storage khi trang được load
    // $scope.cart.loadFromLocalStorage();

    // // Xử lý khi người dùng đăng xuất
    // $scope.logout = function () {
    //     $scope.cart.clearLocalStorage(); // Xóa dữ liệu giỏ hàng khi đăng xuất
    //     // Thực hiện các bước đăng xuất khác ở đây...
    // };

    // // Phương thức thêm sản phẩm vào giỏ hàng
    // $scope.addToCart = function (product_id) {
    //     $scope.cart.add(product_id);
    // };

    // // Phương thức xóa sản phẩm khỏi giỏ hàng
    // $scope.removeFromCart = function (product_id) {
    //     $scope.cart.remove(product_id);
    // };

    // // Phương thức xóa tất cả sản phẩm khỏi giỏ hàng
    // $scope.clearCart = function () {
    //     $scope.cart.clear();
    // };





    // $scope.updateTotal = function () {

    //     $scope.cart.totalOriginalPrice = 0; // Giá gốc
    //     $scope.cart.totalDiscountPercentage = 0; // Tổng % giảm
    //     $scope.cart.totalPriceWithDiscount = 0; // Tổng tiền đã giảm

    //     angular.forEach($scope.cart.items, function (item) {
    //         if (item.checked) {
    //               // Nếu sản phẩm đã được chọn, thêm vào mảng selectedItemsForCheckout
    //         $scope.selectedItemsForCheckout.push(item);

    //             // Tính giá gốc
    //             $scope.cart.totalOriginalPrice += item.quantity * item.unit_price;

    //             // Tính tổng phần trăm đã giảm
    //             $scope.cart.totalDiscountPercentage += item.distcount;

    //             // Tính tổng tiền sau khi tính cả phần trăm giảm
    //             let priceWithoutDiscount = item.quantity * item.unit_price;
    //             let discountAmount = priceWithoutDiscount * item.distcount / 100;
    //             let totalPriceForItem = priceWithoutDiscount - discountAmount;
    //             $scope.cart.totalPriceWithDiscount += totalPriceForItem;
    //         }
    //     });

    // };

    // $scope.redirectToCheckout = function () {
    //     // Lưu giá trị từ cart.totalPriceWithDiscount vào localStorage
    //     localStorage.setItem('totalPrice', $scope.cart.totalPriceWithDiscount);

    //     // Chuyển hướng đến trang checkout
    //     window.location.href = '/order/checkout';
    // };

    // // Trang Checkout
    // // Lấy giá trị từ localStorage khi trang được load
    // $scope.totalPriceForCheckout = localStorage.getItem('totalPrice');

    // // Kiểm tra nếu giá trị không tồn tại hoặc không hợp lệ
    // if (!$scope.totalPriceForCheckout || isNaN($scope.totalPriceForCheckout)) {
    //     // Xử lý khi giá trị không hợp lệ
    //     console.error('Giá trị không hợp lệ');
    // }






    // thanh toan paypal
    $scope.order = {
        createDate: new Date(),
        address: "",
        phone: "",
        status: 0,
        intent: 'Sale',
        method: 'Paypal',
        currency: 'USD',
        description: 'Đã thanh toán',

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

            var order = angular.copy(this);
            $http.post("/rest/orders", order).then(resp => {
                alert("Đặt hàng thành công");
                $scope.cart.clear();
                location.href = "/order/detail/" + resp.data.order_id;
            }).catch(error => {
                alert("Đặt hàng thất bại");
                console.log(error)
            })
        },
        purchase1() {
            var order = angular.copy(this);
            $http.post("/rest/orders", order).then(resp => {
            }).catch(error => {
                alert("Đặt hàng thất bại");
                console.log(error)
            })
        },
        purchase2() {
            $scope.cart.clear();
        }
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
            // alert("Đã thêm sản phẩm vào danh sách yêu thích");
            themYeuThich("success", "Đã thêm sản phẩm vào danh sách yêu thích");

            $scope.isShow = isShow;
        }).catch(error => {
            // alert("Bạn cần đăng nhập để sử dụng chức năng này");
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

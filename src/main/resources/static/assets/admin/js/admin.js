const app = angular.module("admin-ctrl", []);
app.controller("authority", function($scope, $http , $location) {
	$scope.roles=[];
   $scope.admins = [];
   $scope.authorities = [];

   $scope.initialize = function(){
       // load all roles
       $http.get("/rest/roles").then(resp =>{
           $scope.roles = resp.data;
       })

       //load staffs and directors(administrators)
       $http.get("/rest/accounts").then(resp =>{
           $scope.admins = resp.data;
       })

       //load authorities of staffs and directors
       $http.get("/rest/authorities").then(resp =>{
            $scope.authorities = resp.data;
         }).catch(error =>{
             $location.path("/unauthorized");
         })
   }

   $scope.authority_of = function(acc , role){
       if($scope.authorities){
           return $scope.authorities.find(ur => ur.account.username == acc.username 
                                            && ur.role.role_id == role.role_id);
       }
   }
   $scope.authority_changed = function(acc,role){
       var authority = $scope.authority_of(acc , role);
       if(authority){
           $scope.revoke_authority(authority);
       }
       else{
           authority = {account:acc , role : role};
           $scope.grant_authority(authority);
       }
   }

   //thêm mới authority
   $scope.grant_authority = function(authority){
       $http.post(`/rest/authorities`, authority).then(resp =>{
           $scope.authorities.push(resp.data);
           capQuyen("success", "Cấp quyền sử dụng thành công");
       }).catch(error =>{
         
           capQuyen("error", "Cấp quyền thất bại");
           
       })
   }

   //Xoá authority
   $scope.revoke_authority = function(authority){
       $http.delete(`/rest/authorities/${authority.authorize_id}`).then(resp =>{
           var index = $scope.authorities.findIndex(a => a.Authorize_id == authority.authorize_id);
           $scope.authorities.splice(index ,1);
           capQuyen("success", "Thu hồi quyền sử dụng thành công");
       }).catch(error =>{
        capQuyen("error", "Thu hồi quyền sử dụng thất bại");
        console.log("Eror",error );
       })
   }

   $scope.initialize();
    $scope.pager = {
        page: 0,
        size: 10,
        get admins() {
            var start = this.page * this.size;
            return $scope.admins.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1 * $scope.admins.length / this.size);
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
});

app.controller("trademark-ctrl", function($scope, $http) {
    $scope.items = [];

    $scope.form = {};

    $scope.initialize = function() {
        $http.get("/rest/trademarks").then(resp => {
            $scope.items = resp.data;
           
        });
    }
    $scope.initialize();
    $scope.reset = function() {
        $scope.form = {
            
        }
    }
    $scope.edit = function(item) {
        $scope.form = angular.copy(item);
       
    }
    $scope.create = function() {
        var item = angular.copy($scope.form);
        $http.post(`/rest/trademarks`, item).then(resp => {
            resp.data, createDate = new Date(resp.data.createDate);
            $scope.items.push(resp.data);
            $scope.reset();
            // alert("Thêm mới thành công");
            themThuongHieu("success", "Thêm mới thành công")

        }).catch(error => {
            // alert("Thêm mới thất bại");
            console.log("Error", error);
        })
    }
    $scope.update = function(item) {
        var item = angular.copy($scope.form);
        $http.put(`/rest/trademarks/${item.trademark_id}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items[index] = item;
            // alert("Cập nhập thành công");
        }).catch(error => {
            // alert("Cập nhập thất bại");
            console.log("Error", error);
        })
    }
    $scope.delete = function(item) {
        $http.delete(`/rest/trademarks/${item.trademark_id}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items.splice(index, 1);
            $scope.reset();
            alert("Xóa thành công");
        }).catch(error => {
            alert("Không thể xóa , vì vẫn còn hàng");
            console.log("Error", error);
        })
    }
    $scope.pager = {
        page: 0,
        size: 6,
        get items() {
            var start = this.page * this.size;
            return $scope.items.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1 * $scope.items.length / this.size);
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
});
app.controller("category-ctrl", function($scope, $http) {
    $scope.items = [];

    $scope.form = {};

    $scope.initialize = function() {
        $http.get("/rest/categories").then(resp => {
            $scope.items = resp.data;
           
        });
    }
    $scope.initialize();
    $scope.reset = function() {
        $scope.form = {
            
        }
    }
    $scope.edit = function(item) {
        $scope.form = angular.copy(item);
       
    }
    $scope.create = function() {
        var item = angular.copy($scope.form);
        $http.post(`/rest/categories`, item).then(resp => {
            resp.data, createDate = new Date(resp.data.createDate);
            $scope.items.push(resp.data);
            $scope.reset();
            // alert("Thêm mới thành công");
            themThuongHieu("success", "Thêm mới thành công")

        }).catch(error => {
            alert("Lỗi thêm mới loại sản phẩm");
            console.log("Error", error);
        })
    }
    $scope.update = function(item) {
        var item = angular.copy($scope.form);
        $http.put(`/rest/categories/${item.category_id}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items[index] = item;
            alert("Cập nhập thành công");
        }).catch(error => {
            alert("Cập nhập thất bại");
            console.log("Error", error);
        })
    }
    $scope.delete = function(item) {
        $http.delete(`/rest/categories/${item.category_id}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items.splice(index, 1);
            $scope.reset();
            alert("Xóa thành công");
        }).catch(error => {
            alert("Không thể xóa loại sản phẩm vì vẫn còn hàng trong kho");
            console.log("Error", error);
        })
    }
    $scope.pager = {
        page: 0,
        size: 6,
        get items() {
            var start = this.page * this.size;
            return $scope.items.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1 * $scope.items.length / this.size);
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
});

app.controller("orderstatus-ctrl", function($scope, $http) {
    $scope.items = [];
 

    $scope.initialize = function() {
        $http.get("/rest/ordersstatus").then(resp => {
            $scope.items = resp.data;
            
        });
       
    }
    $scope.edit = function(order_id) {
       location.href = "/admin/order/edit?order_id=" + order_id;
       
    }
    $scope.initialize();
    $scope.pager = {
        page: 0,
        size: 10,
        get items() {
            var start = this.page * this.size;
            return $scope.items.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1 * $scope.items.length / this.size);
        },
        get sumorder() {
            return Math.ceil(1 * $scope.items.length);
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
});
app.controller("charuser-ctrl", function($scope, $http) {
    $scope.items = [];
 

    $scope.initialize = function() {
        $http.get("/rest/report1").then(resp => {
            $scope.items = resp.data;
            
        });
       
    }
  
    $scope.initialize();
    $scope.pager = {
        page: 0,
        size: 5,
        get items() {
            var start = this.page * this.size;
            return $scope.items.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1 * $scope.items.length / this.size);
        },
        get sumorder() {
            return Math.ceil(1 * $scope.items.length);
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
});

app.controller("iconadmin-ctrl", function($scope, $http) {
  
 	 $scope.product = function() {
       location.href = "/admin/product/list";
       
    }

    $scope.order = function() {
       location.href = "/admin/order/list";
       
    }
    
     $scope.account = function() {
       location.href = "/admin/account/list";
       
    }
    
     $scope.post = function() {
       location.href = "/admin/post/list";
       
    }
    
 });
 app.controller("ordertop10-ctrl", function($scope, $http) {
    $scope.items = [];
 

    $scope.initialize = function() {
        $http.get("/rest/ordertop10").then(resp => {
            $scope.items = resp.data;
            
        });
       
    }
    $scope.edit = function(order_id) {
       location.href = "/admin/order/edit?order_id=" + order_id;
       
    }
    $scope.initialize();
    
});
app.controller("producttop10-ctrl", function($scope, $http) {
    $scope.items = [];
 

    $scope.initialize = function() {
        $http.get("/rest/producttop10").then(resp => {
            $scope.items = resp.data;
            
        });
       
    }
    $scope.edit = function(product_id) {
       location.href = "/admin/product/edit?product_id=" + product_id;
       
    }
    $scope.initialize();
    
});
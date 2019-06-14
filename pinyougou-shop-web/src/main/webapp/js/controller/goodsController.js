//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, itemCatService, uploadService,
                                            brandService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //初始化entity对象
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};

    /**
     * 保存【新增、修改】
     * 使用了富文本编辑器、图片上传
     */
    $scope.save = function () {
        //将富文本编辑器的内容赋值给商品介绍属性
        $scope.entity.goodsDesc.introduction = editor.html();
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {

            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //如果添加成功，清空富文本编辑器
                    editor.html('');
                    //重新加载
                    $scope.entity = {goods: {}, goodsDesc: {itemImages: []}};
                } else {
                    alert(response.message);
                }
            }
        );
    }
    /**
     * 上传图片到 FASTDFS 图片服务器、调用方法将图片信息绑定到entity对象中
     */
    $scope.upload = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                $scope.image.url = response.message;  //成功的话，message为url地址
            } else {
                alert(response.message)
            }
        })
    }

    /**
     * 将模态框中成功上传到图片服务器的image绑定到entity中
     */
    $scope.saveToImages = function (image) {
        $scope.entity.goodsDesc.itemImages.push(image);
        $scope.image = {};   //清空模态框
    };
    /**
     * 删除所上传的图片列表中的某个图片
     * @param index  列表索引
     */
    $scope.delePic = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };

    /**
     *  显示一级分类,调用findByParentId方法id=0
     */
    $scope.list1ItemCats = function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.item1CatList = response;
            // $scope.itemList = [];
            // $scope.item2CatList = [];
            // $scope.item3CatList = [];
            // $scope.brandList = [];
            // $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};
        })
    };
    /**
     * 监控一级类别变量item1CatId，如果它变化，则将新值newValue作为下一级的parentId
     */
    $scope.$watch("entity.goods.category1Id", function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.item2CatList = response;
            // $scope.itemList = [];
            // $scope.item3CatList = [];
            // $scope.brandList = [];
            // $scope.entity = {goods: {category1Id: newValue}, goodsDesc: {itemImages: [], specificationItems: []}};
        })
    });
    /**
     * 监控二级类别变量item2CatId，如果它变化，则将新值newValue作为下一级的parentId
     */
    $scope.$watch("entity.goods.category2Id", function (newValue, oldValue) {

        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.item3CatList = response;
            // $scope.entity = {
            //     goods: {category1Id: $scope.entity.goods.category1Id, category2Id: newValue},
            //     goodsDesc: {itemImages: [], specificationItems: []}
            // };
            // $scope.brandList = [];
            // $scope.itemList = [];
        })
    });
    /**
     * 监控三级类别变量item3CatId，确定三级分类id后，可以据此确定模板id
     */
    $scope.$watch("entity.goods.category3Id", function (newValue, oldValue) {

        //1.根据分类变量三级id查询一个分类对象itemCat
        itemCatService.findOne(newValue).success(function (response) {
            // $scope.entity = {
            //     goods: {
            //         category1Id: $scope.entity.goods.category1Id,
            //         category2Id: $scope.entity.goods.category2Id,
            //         category3Id: newValue
            //     }, goodsDesc: {itemImages: [], specificationItems: []}
            // };
            //1.1确定模板id
            $scope.entity.goods.typeTemplateId = response.typeId;
        });

    })
    /**
     * 监控模板变量typeTemplateId，获得品牌列表、自定义属性列表、获得规格列表
     */
    $scope.$watch("entity.goods.typeTemplateId", function (newValue, oldValue) {
        //2.根据模板id查询得到一个模板对象typeTemplate
        typeTemplateService.findOne(newValue).success(function (response) {
            //2.1获得品牌列表
            $scope.brandList = JSON.parse(response.brandIds);  //这里注意将字符串转换为数组
            //2.2获得typeTemplateId对应的模板的对应的goodsDec的自定义属性列表
            $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
        });

        //3.根据模板Id查询规格列表,规格列表中必须包含规格选项[服务端提供数据变更]
        typeTemplateService.findSpecById(newValue).success(function (response) {
            //3.1将规格列表赋值给itemList
            $scope.itemList = response;
        })

    });


    /**
     *  点击checkbox创建一条或多条entity.item对象
     * @param event  点击对象本身
     * @param specName  规格名称
     * @param specValue 规格名称对应的值
     * @return Object  返回一个集合中存在的对象
     */
//定义根据key在数组中查询元素的方法
    searchObjectByKey = function (list, key, value) {
        for (var i = 0; i < list.length; i++) {
            if (list[i][key] == value) {
                return list[i];
            }
        }
        return null;
    }
    //点击了规格选项中的某一项后触发的方法（向entity.goodsDesc.specificationItems数组中动态添加或移除数据）
    $scope.updateSelectSpec = function (event, name, value) {
        //1.根据key名为"attributeName"查找在entity.goodsDesc.specificationItems是否有指定对象
        var object = searchObjectByKey($scope.entity.goodsDesc.specificationItems, "attributeName", name);
        //2.判断对象是否存在
        if (object) {		//对象存在，就向$scope.entity.goodsDesc.specificationItems.attributeValue中添加或移除元素
            //2.1)判断是否复选
            if (event.target.checked) { //①如果复选，就向$scope.entity.goodsDesc.specificationItems.attributeValue添加复选项
                object.attributeValue.push(value);
            } else {			//②未被复选，就从$scope.entity.goodsDesc.specificationItems.attributeValue移除一项
                var index = object.attributeValue.indexOf(value);
                object.attributeValue.splice(index, 1);
                //③ 如果移除后$scope.entity.goodsDesc.specificationItems.attributeValue的长度为0（没有值）就从
                //$scope.entity.goodsDesc.specificationItems移除此object对象
                if (object.attributeValue.length == 0) {
                    var index = $scope.entity.goodsDesc.specificationItems.indexOf(object);
                    $scope.entity.goodsDesc.specificationItems.splice(index, 1);
                }
            }
        } else {				//如果对象不存在，就向规格列表中添加对象
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }
        $scope.createSkuList();
    }
    //生成sku商品列表
    $scope.createSkuList = function () {
        //1.定义默认sku商品列表
        $scope.entity.items = [{spec: {}, status: '0', isDefault: '0', price: 100, num: 9999}];
        //2.得到规格选项列表
        var specificationItems = $scope.entity.goodsDesc.specificationItems;
        //3.遍历规格选项列表
        for (var i = 0; i < specificationItems.length; i++) {
            var specItem = specificationItems[i];
            //每次循环就修改规格选项的值
            $scope.entity.items = $scope.addColumn($scope.entity.items, specItem.attributeName, specItem.attributeValue);
        }
    }
    $scope.addColumn = function (specificationItems, attributeName, attributeValue) {
        //1.重新定义新的数组，将新克隆得到的数组放到此数组中
        var specList = [];
        //2.重新遍历数组
        for (var i = 0; i < specificationItems.length; i++) {
            //2.1)得到原始行
            var oldRow = specificationItems[i];
            //2.2)遍历规格选项值数组
            for (var j = 0; j < attributeValue.length; j++) {
                //2.3)对原来的数据进行深克隆得到一新行
                var newRow = JSON.parse(JSON.stringify(oldRow));
                //2.4)对newRow重新赋值
                newRow.spec[attributeName] = attributeValue[j];
                //2.5)再将newRow放到specList数组中
                specList.push(newRow);
            }
        }
        //3.返回新数组
        return specList;
    };


//分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    /**
     * 根据商品列表页面传过来的id查询一个entity对象
     * 对于数据中的集合属性，需要利用JSON转换为js对象，【实际上就是去掉了字符串中的/转义符号】
     */
    $scope.findOne = function () {
        if ($location.search()['id']) {
            goodsService.findOne($location.search()['id']).success(
                function (response) {
                    //1.给entity赋值
                    $scope.entity = response;
                    //2.给富文本编辑器赋值
                    editor.html($scope.entity.goodsDesc.introduction)
                    // 3.图片数组转换
                    $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                    //4.specificationItems
                    $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems)
                    //5.扩展属性
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems)
                    //6.规格选项列表转换
                    for (let i = 0; i < $scope.entity.items.length; i++) {
                        $scope.entity.items[i].spec = JSON.parse($scope.entity.items[i].spec)
                    }
                }
            );
        }
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }
    //定义搜索对象
    $scope.searchEntity = {};
    //声明初始化分类数组
    $scope.categorys = [];

    /**
     * 查询所有分类对象，构建一个分类数组
     */
    $scope.findAllCatgory = function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                $scope.categorys[response[i].id] = response[i].name;
            }
        })
    };


    //声明并初始化分类等级id数组
    $scope.goodsStatus = ['未审核', '审核通过', '申请中', '已驳回'];
    /**
     *  分页查询
     * @param page
     * @param rows
     */
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    /**
     * ng-checked的回调函数，判断在名为specName的属性中是否存在值为value的情况，
     *          存在则返回true,此时，checkbox被选中，不存在则返回false不被选中
     * @param specName  $scope.entity.goodsDesc.specificationItems.specName
     * @param value    $scope.entity.goodsDesc.specificationItems.value
     * @return boolean  存在与否的标记变量
     */
    $scope.chekSpec = function (specName, value) {
        //1.调用  searchObjectByKey(list,key,value)
        var object = searchObjectByKey($scope.entity.goodsDesc.specificationItems, "attributeName", specName);
        //2.判断object是否存在
        if (object==null ) {
            return false;
        } else {
            return object.attributeValue.indexOf(value)>=0;
        }
    }
});	

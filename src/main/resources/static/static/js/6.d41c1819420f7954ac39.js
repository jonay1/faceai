webpackJsonp([6],{I7fx:function(e,t){},QV70:function(e,t,o){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r={name:"Collect",data:function(){return{loading:!1,form:{region:"SH",image:void 0},rules:{phone:[{required:!0,message:"请输入手机号",validator:function(e,t,o){/^1[34578]\d{9}$/.test(t)?o():o(new Error("手机格式不正确"))}}],name:[{required:!0,message:"请输入姓名",validator:function(e,t,o){/^[^\x00-\xff]{2,5}/.test(t)?o():o(new Error("姓名不合法"))}}],region:[{required:!0,message:"请选择区域"}],image:[{required:!0,message:"请上传图片"}]}}},methods:{onFileChange:function(){this.form.image=!0},onFileRemove:function(){this.form.image=void 0},onUploadSuccess:function(e,t,o){-1==e?this.$message.warning("提交失败,请确图片中存在头像"):(this.form.image=t.url,this.$router.push({path:"/collect-result/"+this.form.phone,query:{region:this.form.region}})),this.loading=!1},onSubmit:function(){var e=this;this.$refs.form.validate(function(t){if(!t)return!1;e.loading=!0,e.$refs.upload.submit()})}}},n={render:function(){var e=this,t=e.$createElement,o=e._self._c||t;return o("div",[e._m(0),e._v(" "),o("el-form",{ref:"form",staticClass:"form",attrs:{model:e.form,rules:e.rules}},[o("el-form-item",{attrs:{required:"",prop:"phone"}},[o("el-input",{attrs:{minlength:"11",maxlength:"11",placeholder:"手机号",autofocus:""},model:{value:e.form.phone,callback:function(t){e.$set(e.form,"phone",t)},expression:"form.phone"}})],1),e._v(" "),o("el-form-item",{attrs:{required:"",prop:"name"}},[o("el-input",{attrs:{placeholder:"姓名"},model:{value:e.form.name,callback:function(t){e.$set(e.form,"name",t)},expression:"form.name"}})],1),e._v(" "),o("el-form-item",{attrs:{required:"",prop:"region"}},[o("el-select",{attrs:{placeholder:"请选择年会区域"},model:{value:e.form.region,callback:function(t){e.$set(e.form,"region",t)},expression:"form.region"}},[o("el-option-group",{attrs:{label:"年会区域"}},[o("el-option",{attrs:{label:"上海",value:"SH"}})],1)],1)],1),e._v(" "),o("el-form-item",{attrs:{required:"",prop:"image"}},[o("el-upload",{ref:"upload",attrs:{action:"/backend/upload","list-type":"picture-card",limit:1,data:e.form,"auto-upload":!1,"on-change":e.onFileChange,"on-remove":e.onFileRemove,"on-success":e.onUploadSuccess}},[o("i",{staticClass:"el-icon-plus"})])],1),e._v(" "),o("div",{staticClass:"btn-bar"},[o("el-button",{attrs:{type:"primary",loading:e.loading},on:{click:e.onSubmit}},[e._v("提    交")])],1)],1)],1)},staticRenderFns:[function(){var e=this.$createElement,t=this._self._c||e;return t("div",{staticClass:"container"},[t("h2",[this._v("人脸信息采集")])])}]};var i=o("VU/8")(r,n,!1,function(e){o("I7fx")},"data-v-08432ee8",null);t.default=i.exports}});
//# sourceMappingURL=6.d41c1819420f7954ac39.js.map
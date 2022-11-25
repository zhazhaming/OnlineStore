package com.example.onlinestore.controller;

import com.example.onlinestore.common.ApiRestResponse;
import com.example.onlinestore.common.Constant;
import com.example.onlinestore.exception.OnlineStore_Exception;
import com.example.onlinestore.exception.OnlineStore_ExceptionEnum;
import com.example.onlinestore.model.pojo.Product;
import com.example.onlinestore.model.request.AddProductReq;
import com.example.onlinestore.model.request.UpdateProductReq;
import com.example.onlinestore.service.ProductService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * 后台商品管理
 */
@RestController
public class ProductAdminController {

    @Autowired
    ProductService productService;

    @ApiOperation ("后台增加商品")
    @PostMapping("/admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq){
        productService.add (addProductReq);
        return ApiRestResponse.success ();
    }

    @PostMapping("/admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest, @RequestParam("file")MultipartFile file){
        String filename = file.getOriginalFilename ( );
        //获取后缀
        String suffixName = filename.substring (filename.lastIndexOf ("."));
        //生成UUID
        UUID uuid = UUID.randomUUID ( );
        //生成文件名
        String newFileName = uuid.toString ()+suffixName;
        //上传路径
        File fileDirectory = new File (Constant.FILER_UPLOAD_DIR);
        //上传图片完整路径
        File destFile = new File (Constant.FILER_UPLOAD_DIR + newFileName);
        if (!fileDirectory.exists ()){
           if (!fileDirectory.mkdir ()) throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.MKDIR_FAILE);
       }
        try {
            //将请求参数file写到本地destFile中
            file.transferTo (destFile);
        } catch (IOException e) {
            e.printStackTrace ( );
        }
        try {
            return  ApiRestResponse.success (getHost (new URI (httpServletRequest.getRequestURL ()+""))+"/images/"+newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.UPLOAD_FAILE);
        }
    }

    //获取返回的ip和端口号
    private URI getHost(URI uri){
        URI effectiveURI;
        try {
            effectiveURI = new URI (uri.getScheme (),uri.getRawUserInfo (),uri.getHost (),uri.getPort (),null,null,null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    @ApiOperation ("后台更新商品")
    @PostMapping("/admin/product/update")
    public ApiRestResponse update(@Valid @RequestBody UpdateProductReq updateProductReq){
        Product product = new Product ();
        BeanUtils.copyProperties (updateProductReq,product);
        productService.update (product);
        return ApiRestResponse.success ();
    }

    @ApiOperation ("后台删除商品")
    @PostMapping("/admin/product/delete")
    public ApiRestResponse delete(@RequestParam Integer id ){
        productService.delete (id);
        return ApiRestResponse.success ();
    }

    @ApiOperation ("后台批量更新商品")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids,@RequestParam Integer sellStatus){
        productService.batchUpdateSellStatus (ids,sellStatus);
        return ApiRestResponse.success ();
    }

    @ApiOperation ("后台商品列表")
    @GetMapping("/admin/product/list")
    public ApiRestResponse list(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        PageInfo pageInfo = productService.listForadmin (pageNum, pageSize);
        return ApiRestResponse.success (pageInfo);
    }
}

package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端-地址薄接口")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    @PostMapping
    @ApiOperation("新增地址")
    public Result add(@RequestBody AddressBook addressBook){
        addressBookService.add(addressBook);
        return Result.success();
    }
    @GetMapping("/list")
    @ApiOperation("查询当前用户的所有地址信息")
    public Result<List<AddressBook>> list(){
        Long userId = BaseContext.getCurrentId();
        AddressBook build = AddressBook.builder().userId(userId).build();
        List<AddressBook> list = addressBookService.list(build);
        return Result.success(list);
    }
    @GetMapping("/default")
    @ApiOperation("查询当前用户的默认地址")
    public Result<AddressBook> getByDefault(){
        Long userId = BaseContext.getCurrentId();
        AddressBook build = AddressBook.builder().userId(userId).isDefault(1).build();
        List<AddressBook> list = addressBookService.list(build);
        if(list.size()>0){return Result.success(list.get(0));}
            else{
                return Result.error("当前没有默认地址");
            }
        }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result updateById(@RequestBody AddressBook addressBook){
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.update(addressBook);
        return Result.success();
    }
    @DeleteMapping
    @ApiOperation("根据Id删除地址")
    public Result deleteById(Long id){
        addressBookService.deleteById(id);
        return Result.success();
    }
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefalut(@RequestBody AddressBook addressBook){
        addressBookService.setDefalut(addressBook);
        return Result.success();
    }

    }




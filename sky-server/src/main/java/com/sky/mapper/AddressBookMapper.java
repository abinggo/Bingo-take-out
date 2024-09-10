package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    /**
     * 新增地址信息
     * @param addressBook
     */
    void insert(AddressBook addressBook);

    /**
     * 根据用户Id查询地址信息
     * @param build
     * @return
     */
    List<AddressBook> list(AddressBook build);

    /**
     * 根据用户id修改信息
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据用户id查询地址信息
     * @param id
     * @return
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    /**
     * 根据id删除地址信息
     * @param id
     */
    @Delete("delete from address_book where  id = #{id}")
    void deleteById(Long id);

    /**
     * 将全部设置为非默认地址
     * @param addressBook
     */
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void updateIsDefaultByUserId(AddressBook addressBook);
}

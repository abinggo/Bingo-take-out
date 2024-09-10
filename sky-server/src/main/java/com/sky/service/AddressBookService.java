package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void add(AddressBook addressBook);

    /**
     * 查询当前用户的所有地址信息
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 根据用户Id修改信息
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id查询地址信息
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 根据id删除地址
     * @param id
     */
    void deleteById(Long id);

    void setDefalut(AddressBook addressBook);
}

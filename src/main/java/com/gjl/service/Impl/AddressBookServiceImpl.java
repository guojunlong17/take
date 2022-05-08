package com.gjl.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjl.domain.AddressBook;
import com.gjl.mapper.AddressBookMapper;
import com.gjl.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}

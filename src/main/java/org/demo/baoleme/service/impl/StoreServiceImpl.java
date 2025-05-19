package org.demo.baoleme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.demo.baoleme.mapper.StoreMapper;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.service.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreMapper storeMapper;

    public StoreServiceImpl(StoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    @Override
    @Transactional
    public Store createStore(Store store) {
        int rows = storeMapper.insert(store);
        return rows > 0 ? store : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Store getStoreById(Long storeId) {
        return storeMapper.selectById(storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Store> getStoresByMerchant(Long merchantId) {
        return storeMapper.selectList(new LambdaQueryWrapper<Store>()
                .eq(Store::getMerchantId, merchantId));
    }

    @Override
    @Transactional
    public boolean updateStore(Store store) {
        return storeMapper.updateById(store) > 0;
    }

    @Override
    @Transactional
    public boolean toggleStoreStatus(Long storeId, int status) {
        Store store = new Store();
        store.setId(storeId);
        store.setStatus(status);
        return storeMapper.updateById(store) > 0;
    }

    @Override
    @Transactional
    public boolean deleteStore(Long storeId) {
        return storeMapper.deleteById(storeId) > 0;
    }
}
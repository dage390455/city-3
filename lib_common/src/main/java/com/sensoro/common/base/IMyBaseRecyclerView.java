package com.sensoro.common.base;

import java.util.List;

public interface IMyBaseRecyclerView<D> {
    void updateAdapter(List<D> list);

    List<D> getAdapterData();

}

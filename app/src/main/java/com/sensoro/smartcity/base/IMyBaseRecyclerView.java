package com.sensoro.smartcity.base;

import java.util.List;

public interface IMyBaseRecyclerView<D> {
    void updateAdapter(List<D> list);

    List<D> getAdapterData();

}

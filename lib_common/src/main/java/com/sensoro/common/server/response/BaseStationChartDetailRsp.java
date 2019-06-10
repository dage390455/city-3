package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.BaseStationChartDetailModel;

import java.util.List;

public class BaseStationChartDetailRsp extends ResponseBase {


    public List<BaseStationChartDetailModel> getData() {
        return data;
    }

    public void setData(List<BaseStationChartDetailModel> data) {
        this.data = data;
    }

    private List<BaseStationChartDetailModel> data;
}

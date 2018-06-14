package com.sensoro.smartcity.temp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mobstat.StatService;
import com.fengmap.android.FMErrorMsg;
import com.fengmap.android.data.OnFMDownloadProgressListener;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.FMPickMapCoordResult;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.event.OnFMMapClickListener;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLocationLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMNode;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.util.FileUtil;
import com.sensoro.smartcity.util.ViewHelper;

import java.util.Collection;

import mabbas007.tagsedittext.TagsEditText;

/**
 * 基础地图显示
 * <p>地图加载完成地图后，注意在页面销毁时使用{@link FMMap#onDestroy()}释放地图资源
 *
 * @author hezutao@fengmap.com
 * @version 2.0.0
 */
public class FMMapBasic extends AppCompatActivity implements OnFMMapInitListener, OnFMMapClickListener, TagsEditText
        .TagsEditListener {

    private FMMap mMap;
    private FMLocationLayer mLocationLayer;
    private FMLocationMarker mLocationMarker;
    private FMImageLayer mImageLayer;
    private TagsEditText tagsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_basic);
        StatService.setDebugOn(true);
        StatService.start(this);
        openMapByPath();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            tagsEditText.showDropDown();
        }
    }


    /**
     * 加载地图数据
     */
    private void openMapByPath() {

        FMMapView mapView = (FMMapView) findViewById(R.id.map_view);
        tagsEditText = (TagsEditText) findViewById(R.id.tet);
//        tagsEditText.setTags();
        tagsEditText.getTags();
        tagsEditText.setTagsListener(this);
        tagsEditText.onSaveInstanceState();
        mMap = mapView.getFMMap();
        mMap.setOnFMMapInitListener(this);
        //加载离线数据
        String path = FileUtil.getDefaultMapPath(this);
        mMap.openMapByPath(path);
//         openMapById(id,true) 加载在线地图数据，并自动更新地图数据
//         mMap.openMapById(FileUtils.DEFAULT_MAP_ID,true);
        mLocationLayer = mMap.getFMLayerProxy().getFMLocationLayer();
        mMap.addLayer(mLocationLayer);
        int groupId = mMap.getFocusGroupId();
        //
        int[] showFloors = mMap.getMapGroupIds();
        int focus = 0;                                   //默认焦点层
//        mMap.setMultiDisplay(showFloors, focus, new OnFMSwitchGroupListener() {
//            @Override
//            public void beforeGroupChanged() {
//
//            }
//
//            @Override
//            public void afterGroupChanged() {
//
//            }
//        });  //设置楼层多层显示

//单楼层显示地图，默认显示第一层
//        int showFloors = mMap.fo;
//        int focus =0;                                   //默认焦点层
//        mMap.setMultiDisplay(showFloors,focus,null);
        //
        FMModelLayer modelLayer = mMap.getFMLayerProxy().getFMModelLayer(groupId);//获取焦点模型图层
        mLocationLayer.setOnFMNodeListener(new OnFMNodeListener() {
            @Override
            public boolean onClick(FMNode node) {
                //设置定位点图片
                mLocationMarker = (FMLocationMarker) node;
                mLocationMarker.setActiveImageFromAssets("active.png");
                mLocationMarker.setStaticImageFromAssets("active.png");
//设置定位图片宽高
                mLocationMarker.setMarkerWidth(30);
                mLocationMarker.setMarkerHeight(30);
                mLocationLayer.addMarker(mLocationMarker);
                return false;
            }

            @Override
            public boolean onLongPress(FMNode node) {
                return false;
            }
        });
//        mLocationLayer.
        //
        //
//        FMMapCoord centerCoord = new FMMapCoord(1.296164E7, 4861845.0);
//        mLocationMarker = new FMLocationMarker(groupId, centerCoord);

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
        mMap.onResume();
    }


    /**
     * 地图加载成功回调事件
     *
     * @param path 地图所在sdcard路径
     */
    @Override
    public void onMapInitSuccess(String path) {
        //加载离线主题文件
        mMap.loadThemeByPath(FileUtil.getDefaultThemePath(this));

        //加载在线主题文件
        //mMap.loadThemeById(FMMap.DEFAULT_THEME_CANDY);
        mMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D);

        //图片图层
        mImageLayer = mMap.getFMLayerProxy().createFMImageLayer(mMap.getFocusGroupId());
        mMap.addLayer(mImageLayer);
        mMap.setOnFMMapClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    /**
     * 地图加载失败回调事件
     *
     * @param path      地图所在sdcard路径
     * @param errorCode 失败加载错误码，可以通过{@link FMErrorMsg#getErrorMsg(int)}获取加载地图失败详情
     */
    @Override
    public void onMapInitFailure(String path, int errorCode) {
        //TODO 可以提示用户地图加载失败原因，进行地图加载失败处理
    }

    /**
     * 当{@link FMMap#openMapById(String, boolean)}设置openMapById(String, false)时地图不自动更新会
     * 回调此事件，可以调用{@link FMMap#upgrade(FMMapUpgradeInfo, OnFMDownloadProgressListener)}进行
     * 地图下载更新
     *
     * @param upgradeInfo 地图版本更新详情,地图版本号{@link FMMapUpgradeInfo#getVersion()},<br/>
     *                    地图id{@link FMMapUpgradeInfo#getMapId()}
     * @return 如果调用了{@link FMMap#upgrade(FMMapUpgradeInfo, OnFMDownloadProgressListener)}地图下载更新，
     * 返回值return true,因为{@link FMMap#upgrade(FMMapUpgradeInfo, OnFMDownloadProgressListener)}
     * 会自动下载更新地图，更新完成后会加载地图;否则return false。
     */
    @Override
    public boolean onUpgrade(FMMapUpgradeInfo upgradeInfo) {
        //TODO 获取到最新地图更新的信息，可以进行地图的下载操作
        return false;
    }

    /**
     * 地图销毁调用
     */
    @Override
    public void onBackPressed() {
        if (mMap != null) {
            mMap.onDestroy();
        }
        super.onBackPressed();
    }

    @Override
    public void onMapClick(float v, float v1) {
        //地图拾取对象
        FMPickMapCoordResult mapCoordResult = mMap.pickMapCoord(v, v1);
        if (mapCoordResult != null) {
            //添加图片标注
            FMImageMarker imageMarker = ViewHelper.buildImageMarker(getResources(),
                    mapCoordResult.getMapCoord(), R.drawable.ic_marker_blue);
            mImageLayer.addMarker(imageMarker);
        }
    }

    @Override
    public void onTagsChanged(Collection<String> tags) {

    }

    @Override
    public void onEditingFinished() {

    }

    @Override
    public void onTagDuplicate() {

    }
}

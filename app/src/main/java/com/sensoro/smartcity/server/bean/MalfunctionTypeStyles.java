package com.sensoro.smartcity.server.bean;

public class MalfunctionTypeStyles {
    /**
     * id : unknownSensor
     * errorType : 5010000
     * name : 传感器故障(未知)
     * malfunctionmsg : 传感器发生故障
     * recoverymsg : 传感器故障解除
     */
    //TODO 减少序列化,暂时只用name
//    private String id;
//    private int errorType;
    private String name;
//    private String malfunctionmsg;
//    private String recoverymsg;

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public int getErrorType() {
//        return errorType;
//    }
//
//    public void setErrorType(int errorType) {
//        this.errorType = errorType;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getMalfunctionmsg() {
//        return malfunctionmsg;
//    }
//
//    public void setMalfunctionmsg(String malfunctionmsg) {
//        this.malfunctionmsg = malfunctionmsg;
//    }
//
//    public String getRecoverymsg() {
//        return recoverymsg;
//    }
//
//    public void setRecoverymsg(String recoverymsg) {
//        this.recoverymsg = recoverymsg;
//    }

    @Override
    public String toString() {
        return "MalfunctionTypeStyles{" +
//                "id='" + id + '\'' +
//                ", errorType=" + errorType +
                ", name='" + name + '\'' +
//                ", malfunctionmsg='" + malfunctionmsg + '\'' +
//                ", recoverymsg='" + recoverymsg + '\'' +
                '}';
    }
}

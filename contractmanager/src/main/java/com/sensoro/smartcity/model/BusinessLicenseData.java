package com.sensoro.smartcity.model;

public class BusinessLicenseData {
    /**
     * log_id : 7330434043510039211
     * direction : 0
     * words_result_num : 7
     * words_result : {"社会信用代码":{"place":{"width":0,"top":0,"height":0,"left":0},"words":"无"},
     * "单位名称":{"place":{"width":165,"top":244,"height":37,"left":147},"words":"咸海九鼎的织服装有限公司"},
     * "法人":{"place":{"width":50,"top":308,"height":26,"left":145},"words":"郭云丽"},"证件编号":{"place":{"width":79,
     * "top":217,"height":6,"left":569},"words":"7103020001133"},"成立日期":{"place":{"width":0,"top":0,"height":0,
     * "left":0},"words":"无"},"地址":{"place":{"width":132,"top":276,"height":30,"left":147},"words":"威海市世品大道7"},
     * "有效期":{"place":{"width":0,"top":0,"height":0,"left":0},"words":"无"}}
     */

    private long log_id;
    private int direction;
    private int words_result_num;
    private WordsResultBean words_result;

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getWords_result_num() {
        return words_result_num;
    }

    public void setWords_result_num(int words_result_num) {
        this.words_result_num = words_result_num;
    }

    public WordsResultBean getWords_result() {
        return words_result;
    }

    public void setWords_result(WordsResultBean words_result) {
        this.words_result = words_result;
    }

    public static class WordsResultBean {
        /**
         * 社会信用代码 : {"place":{"width":0,"top":0,"height":0,"left":0},"words":"无"}
         * 单位名称 : {"place":{"width":165,"top":244,"height":37,"left":147},"words":"咸海九鼎的织服装有限公司"}
         * 法人 : {"place":{"width":50,"top":308,"height":26,"left":145},"words":"郭云丽"}
         * 证件编号 : {"place":{"width":79,"top":217,"height":6,"left":569},"words":"7103020001133"}
         * 成立日期 : {"place":{"width":0,"top":0,"height":0,"left":0},"words":"无"}
         * 地址 : {"place":{"width":132,"top":276,"height":30,"left":147},"words":"威海市世品大道7"}
         * 有效期 : {"place":{"width":0,"top":0,"height":0,"left":0},"words":"无"}
         */

        private 社会信用代码Bean 社会信用代码;
        private 单位名称Bean 单位名称;
        private 法人Bean 法人;
        private 证件编号Bean 证件编号;
        private 成立日期Bean 成立日期;
        private 地址Bean 地址;
        private 有效期Bean 有效期;

        public 社会信用代码Bean get社会信用代码() {
            return 社会信用代码;
        }

        public void set社会信用代码(社会信用代码Bean 社会信用代码) {
            this.社会信用代码 = 社会信用代码;
        }

        public 单位名称Bean get单位名称() {
            return 单位名称;
        }

        public void set单位名称(单位名称Bean 单位名称) {
            this.单位名称 = 单位名称;
        }

        public 法人Bean get法人() {
            return 法人;
        }

        public void set法人(法人Bean 法人) {
            this.法人 = 法人;
        }

        public 证件编号Bean get证件编号() {
            return 证件编号;
        }

        public void set证件编号(证件编号Bean 证件编号) {
            this.证件编号 = 证件编号;
        }

        public 成立日期Bean get成立日期() {
            return 成立日期;
        }

        public void set成立日期(成立日期Bean 成立日期) {
            this.成立日期 = 成立日期;
        }

        public 地址Bean get地址() {
            return 地址;
        }

        public void set地址(地址Bean 地址) {
            this.地址 = 地址;
        }

        public 有效期Bean get有效期() {
            return 有效期;
        }

        public void set有效期(有效期Bean 有效期) {
            this.有效期 = 有效期;
        }

        public static class 社会信用代码Bean {
            /**
             * place : {"width":0,"top":0,"height":0,"left":0}
             * words : 无
             */

            private LocationBean location;
            private String words;

            public LocationBean getLocation() {
                return location;
            }

            public void setLocation(LocationBean location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBean {
                /**
                 * width : 0
                 * top : 0
                 * height : 0
                 * left : 0
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }

        public static class 单位名称Bean {
            /**
             * place : {"width":165,"top":244,"height":37,"left":147}
             * words : 咸海九鼎的织服装有限公司
             */

            private LocationBeanX location;
            private String words;

            public LocationBeanX getLocation() {
                return location;
            }

            public void setLocation(LocationBeanX location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBeanX {
                /**
                 * width : 165
                 * top : 244
                 * height : 37
                 * left : 147
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }

        public static class 法人Bean {
            /**
             * place : {"width":50,"top":308,"height":26,"left":145}
             * words : 郭云丽
             */

            private LocationBeanXX location;
            private String words;

            public LocationBeanXX getLocation() {
                return location;
            }

            public void setLocation(LocationBeanXX location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBeanXX {
                /**
                 * width : 50
                 * top : 308
                 * height : 26
                 * left : 145
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }

        public static class 证件编号Bean {
            /**
             * place : {"width":79,"top":217,"height":6,"left":569}
             * words : 7103020001133
             */

            private LocationBeanXXX location;
            private String words;

            public LocationBeanXXX getLocation() {
                return location;
            }

            public void setLocation(LocationBeanXXX location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBeanXXX {
                /**
                 * width : 79
                 * top : 217
                 * height : 6
                 * left : 569
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }

        public static class 成立日期Bean {
            /**
             * place : {"width":0,"top":0,"height":0,"left":0}
             * words : 无
             */

            private LocationBeanXXXX location;
            private String words;

            public LocationBeanXXXX getLocation() {
                return location;
            }

            public void setLocation(LocationBeanXXXX location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBeanXXXX {
                /**
                 * width : 0
                 * top : 0
                 * height : 0
                 * left : 0
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }

        public static class 地址Bean {
            /**
             * place : {"width":132,"top":276,"height":30,"left":147}
             * words : 威海市世品大道7
             */

            private LocationBeanXXXXX location;
            private String words;

            public LocationBeanXXXXX getLocation() {
                return location;
            }

            public void setLocation(LocationBeanXXXXX location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBeanXXXXX {
                /**
                 * width : 132
                 * top : 276
                 * height : 30
                 * left : 147
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }

        public static class 有效期Bean {
            /**
             * place : {"width":0,"top":0,"height":0,"left":0}
             * words : 无
             */

            private LocationBeanXXXXXX location;
            private String words;

            public LocationBeanXXXXXX getLocation() {
                return location;
            }

            public void setLocation(LocationBeanXXXXXX location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBeanXXXXXX {
                /**
                 * width : 0
                 * top : 0
                 * height : 0
                 * left : 0
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }
    }
}

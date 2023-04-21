package com.example.weatherdemo.model;

import java.util.List;

public class ApiResponse{
    //返回状态
    private String status;
    private String count;
    private String info;
    //返回状态说明,10000代表正确
    private String infocode;

    private List<Lives> lives;
    private List<Forecast> forecasts;

    public List<Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public List<Lives> getLives() {
        return lives;
    }

    public void setLives(List<Lives> lives) {
        this.lives = lives;
    }


    public static class Forecast{

        private String city;
        private String adcode;
        private String province;
        //预报发布时间
        private String reporttime;

        public List<Casts> casts;

        public List<Casts> getCasts() {
            return casts;
        }

        public void setCasts(List<Casts> casts) {
            this.casts = casts;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getAdcode() {
            return adcode;
        }

        public void setAdcode(String adcode) {
            this.adcode = adcode;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getReporttime() {
            return reporttime;
        }

        public void setReporttime(String reporttime) {
            this.reporttime = reporttime;
        }
    }

    public static class Casts{

        //日期
        private String date;
        //星期几
        private String week;
        //白天天气现象
        private String dayweather;
        //晚上天气现象
        private String nightweather;
        //白天温度
        private String daytemp;
        //晚上温度
        private String nighttemp;
        //白天风向
        private String daywind;
        //白天风力
        private String daypower;
        //晚上风力
        private String nightpower;

        public String getDaypower() {
            return daypower;
        }

        public void setDaypower(String daypower) {
            this.daypower = daypower;
        }

        public String getNightpower() {
            return nightpower;
        }

        public void setNightpower(String nightpower) {
            this.nightpower = nightpower;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public String getDayweather() {
            return dayweather;
        }

        public void setDayweather(String dayweather) {
            this.dayweather = dayweather;
        }

        public String getNightweather() {
            return nightweather;
        }

        public void setNightweather(String nightweather) {
            this.nightweather = nightweather;
        }

        public String getDaytemp() {
            return daytemp;
        }

        public void setDaytemp(String daytemp) {
            this.daytemp = daytemp;
        }

        public String getNighttemp() {
            return nighttemp;
        }

        public void setNighttemp(String nighttemp) {
            this.nighttemp = nighttemp;
        }

        public String getDaywind() {
            return daywind;
        }

        public void setDaywind(String daywind) {
            this.daywind = daywind;
        }
    }

    public static class Lives{
        //省
        private String province;
        //城市
        private String city;
        //地区
        private String adcode;
        //天气现象（汉字描述）
        private String weather;
        //实时气温，单位：摄氏度
        private String temperature;
        //风向描述
        private String winddirection;
        //风力级别，单位：级
        private String windpower;
        //空气湿度
        private String humidity;
        //数据发布的时间
        private String reporttime;

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getAdcode() {
            return adcode;
        }

        public void setAdcode(String adcode) {
            this.adcode = adcode;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public String getWinddirection() {
            return winddirection;
        }

        public void setWinddirection(String winddirection) {
            this.winddirection = winddirection;
        }

        public String getWindpower() {
            return windpower;
        }

        public void setWindpower(String windpower) {
            this.windpower = windpower;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public String getReporttime() {
            return reporttime;
        }

        public void setReporttime(String reporttime) {
            this.reporttime = reporttime;
        }
    }


}

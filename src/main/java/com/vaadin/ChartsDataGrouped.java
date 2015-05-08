package com.vaadin;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChartsDataGrouped extends ChartsData {

    private final Map<String, Double> weatherMeanTempByMonth;
    private final Map<String, List<WeatherInfo>> weatherByMonth;

    public ChartsDataGrouped() {
        super();

        weatherByMonth = new LinkedHashMap<String, List<WeatherInfo>>();

        for (WeatherInfo wi : getWeatherData()) {
            String month = new SimpleDateFormat("MMMM").format(wi.getDate());

            if (!weatherByMonth.containsKey(month)) {
                weatherByMonth.put(month, new LinkedList<WeatherInfo>());
            }

            weatherByMonth.get(month).add(wi);
        }

        weatherMeanTempByMonth = new LinkedHashMap<String, Double>();

        for (Map.Entry<String, List<WeatherInfo>> month : weatherByMonth
                .entrySet()) {
            double mean = 0;
            for (WeatherInfo wi : month.getValue()) {
                mean += wi.getMeanTemp();
            }
            mean /= month.getValue().size();

            weatherMeanTempByMonth.put(month.getKey(), mean);
        }
    }

    public Map<String, Double> getWeatherMeanTempByMonth() {
        return weatherMeanTempByMonth;
    }

    public Map<String, List<WeatherInfo>> getWeatherByMonth() {
        return weatherByMonth;
    }
}

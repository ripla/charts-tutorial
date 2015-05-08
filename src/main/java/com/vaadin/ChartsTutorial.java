package com.vaadin;

import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.ContainerDataSeries;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Series;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
public class ChartsTutorial extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ChartsTutorial.class, widgetset = "com.vaadin.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        ChartsDataGrouped data = new ChartsDataGrouped();
        layout.addComponent(getWeatherChart(data));
    }

    private Chart getWeatherChart(ChartsDataGrouped data) {
        Chart chart = new Chart();
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Monthly mean temperatures in Turku, Finland 2013");

        conf.getChart().setType(ChartType.LINE);

        DataSeries temp = createDrillDownDataSeriesSync(data);

        conf.getxAxis().setTitle("Month");
        conf.getxAxis().setType(AxisType.CATEGORY);
        conf.getyAxis().setTitle("Temperature (°C)");

        conf.addSeries(temp);

        return chart;
    }

    private DataSeries createDrillDownDataSeriesSync(ChartsDataGrouped data) {
        DataSeries temp = new DataSeries();
        temp.setName("Temperature");

        for (Map.Entry<String, Double> entry : data.getWeatherMeanTempByMonth()
                .entrySet()) {
            String month = entry.getKey();
            double meanForMonth = entry.getValue();
            DataSeriesItem dsi = new DataSeriesItem(month, meanForMonth);

            ContainerDataSeries drillDownSeries = createSeriesForMonth(data,
                    month);

            dsi.addItemWithDrillDown(dsi, drillDownSeries);
        }

        return temp;
    }

    private DataSeries createDrillDownDataSeriesAsync(Chart chart,
            final ChartsDataGrouped data) {
        DataSeries temp = new DataSeries();
        temp.setName("Temperature");

        for (Map.Entry<String, Double> entry : data.getWeatherMeanTempByMonth()
                .entrySet()) {
            String month = entry.getKey();
            double meanForMonth = entry.getValue();
            DataSeriesItem dsi = new DataSeriesItem(month, meanForMonth);
            dsi.setId(month);
            dsi.addItemWithDrillDown(dsi);
        }

        chart.setDrilldownCallback(new DrilldownCallback() {

            @Override
            public Series handleDrilldown(DrilldownEvent event) {
                return createSeriesForMonth(data, event.getItem().getId());
            }
        });

        return temp;
    }

    private ContainerDataSeries createSeriesForMonth(ChartsDataGrouped data,
            String month) {
        List<ChartsData.WeatherInfo> weatherInfosForThisMonth = data
                .getWeatherByMonth().get(month);
        BeanItemContainer<ChartsData.WeatherInfo> weatherInfoContainer = new BeanItemContainer<ChartsData.WeatherInfo>(
                ChartsData.WeatherInfo.class, weatherInfosForThisMonth);
        ContainerDataSeries drillDownSeries = new ContainerDataSeries(
                weatherInfoContainer);
        drillDownSeries.setXPropertyId("date");
        drillDownSeries.setYPropertyId("meanTemp");
        return drillDownSeries;
    }
}

package report.graphic;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

import java.awt.Color;

/**
 * Action used for Grinder report on project level.
 *
 * @author Eivind B Waaler
 */
public class GrinderProjectAction extends AbstractGrinderAction {

   public JFreeChart createMeanTimeGraph() {
      return createNumberBuildGraph(Test.MEAN_TEST_TIME, "Time (ms)");
   }

   public JFreeChart createStdDevGraph() {
      return createNumberBuildGraph(Test.TEST_TIME_STANDARD_DEVIATION, "Time (ms)");
   }

   public JFreeChart createMeanRespLengthGraph() {
      return createNumberBuildGraph(Test.MEAN_RESPONSE_LENGTH, "Length (bytes)");
   }

   private JFreeChart createNumberBuildGraph(String valueName, String unitName) {
	  DefaultCategoryDataset builder = new DefaultCategoryDataset();

      JFreeChart chart = ChartFactory.createStackedAreaChart(
         valueName + " Trend",
         "Build",
         unitName,
         builder,
         PlotOrientation.VERTICAL,
         false,
         false,
         false);

      chart.setBackgroundPaint(Color.WHITE);

      CategoryPlot plot = chart.getCategoryPlot();
      plot.setBackgroundPaint(Color.WHITE);
      plot.setOutlinePaint(null);
      plot.setForegroundAlpha(0.8f);
      plot.setRangeGridlinesVisible(true);
      plot.setRangeGridlinePaint(Color.black);

      CategoryAxis domainAxis = new CategoryAxis(null);
      plot.setDomainAxis(domainAxis);
      domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
      domainAxis.setLowerMargin(0.0);
      domainAxis.setUpperMargin(0.0);
      domainAxis.setCategoryMargin(0.0);

      CategoryItemRenderer renderer = plot.getRenderer();
      renderer.setSeriesPaint(2, Color.RED);
      renderer.setSeriesPaint(1, Color.YELLOW);
      renderer.setSeriesPaint(0, Color.BLUE);

      NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
      rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

      // crop extra space around the graph
      plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

      return chart;
   }

}

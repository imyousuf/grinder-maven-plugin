package report.graphic;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;

import java.awt.Color;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Action used for Grinder report on build level.
 *
 * @author Eivind B Waaler
 */
public class GrinderBuildAction extends AbstractGrinderAction {
	
   private Test totals;
   private List<Test> tests;

   public GrinderBuildAction(InputStream is, PrintStream logger) {
      ResultReader rs = new ResultReader(is, logger);
      totals = rs.getTotals();
      tests = rs.getTests();
      logger.println("Created Grinder results");
   }

   public Test getTotals() {
      return totals;
   }

   public List<Test> getTests() {
      return tests;
   }

   public JFreeChart createTestGraph() {
      DefaultStatisticalCategoryDataset timeDS = new DefaultStatisticalCategoryDataset();
      DefaultCategoryDataset lengthDS = new DefaultCategoryDataset();

      for (Test test : tests) {
         timeDS.add(test.getMeanTime(), test.getStdDev(), Test.MEAN_TEST_TIME, test.getId());
         lengthDS.addValue(test.getMeanRespLength(), Test.MEAN_RESPONSE_LENGTH, test.getId());
      }

      final CategoryAxis xAxis = new CategoryAxis("Test name");
      xAxis.setLowerMargin(0.01);
      xAxis.setUpperMargin(0.01);
      xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
      xAxis.setMaximumCategoryLabelLines(3);

      final ValueAxis timeAxis = new NumberAxis("Time (ms)");
      final ValueAxis lengthAxis = new NumberAxis("Length (bytes)");

      final BarRenderer timeRenderer = new StatisticalBarRenderer();
      timeRenderer.setSeriesPaint(2, Color.RED);
      timeRenderer.setSeriesPaint(1, Color.YELLOW);
      timeRenderer.setSeriesPaint(0, Color.BLUE);
      timeRenderer.setItemMargin(0.0);

      final CategoryPlot plot = new CategoryPlot(timeDS, xAxis, timeAxis, timeRenderer);
      plot.setBackgroundPaint(Color.WHITE);
      plot.setOutlinePaint(null);
      plot.setForegroundAlpha(0.8f);
      plot.setRangeGridlinesVisible(true);
      plot.setRangeGridlinePaint(Color.black);

      final CategoryItemRenderer lengthRenderer = new LineAndShapeRenderer();
      plot.setRangeAxis(1, lengthAxis);
      plot.setDataset(1, lengthDS);
      plot.mapDatasetToRangeAxis(1, 1);
      plot.setRenderer(1, lengthRenderer);

      JFreeChart chart = new JFreeChart("Test time", plot);
      chart.setBackgroundPaint(Color.WHITE);

      return chart;
   }

}

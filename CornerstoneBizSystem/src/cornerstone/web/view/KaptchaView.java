package cornerstone.web.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.github.bingoohuang.patchca.background.BackgroundFactory;
import com.github.bingoohuang.patchca.color.RandomColorFactory;
import com.github.bingoohuang.patchca.custom.ConfigurableCaptchaService;
import com.github.bingoohuang.patchca.filter.ConfigurableFilterFactory;
import com.github.bingoohuang.patchca.filter.library.AbstractImageOp;
import com.github.bingoohuang.patchca.filter.library.WobbleImageOp;
import com.github.bingoohuang.patchca.font.RandomFontFactory;
import com.github.bingoohuang.patchca.text.renderer.BestFitTextRenderer;
import com.github.bingoohuang.patchca.text.renderer.TextRenderer;
import com.github.bingoohuang.patchca.utils.encoder.EncoderHelper;
import com.github.bingoohuang.patchca.word.RandomWordFactory;

import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.View;
import jazmin.util.IOUtil;

/**
 * @author yama
 *
 */
public class KaptchaView extends ConfigurableCaptchaService implements View {
	//
	ByteArrayOutputStream bos;
	public String getCode() throws IOException {
		init();
		bos=new ByteArrayOutputStream();
	    return EncoderHelper.getChallangeAndWriteImage(this, "png", bos);
	}
	//
	@Override
	public void render(Context ctx) throws IOException {
		HttpServletResponse response=ctx.response().raw();
		ServletOutputStream outStream = response.getOutputStream();
        String	mimetype = "image/jpg";
		response.setContentType(mimetype);
        response.setContentLengthLong(bos.size());
        // sets HTTP header
        String ff = new String("kaptcha".getBytes("UTF-8"), "ISO8859-1");
        response.setHeader("Content-Disposition", 
        		"attachment; filename=\"" + ff + "\"");
        ByteArrayInputStream fis=new ByteArrayInputStream(bos.toByteArray());
        IOUtil.copy(fis,outStream);
        IOUtil.closeQuietly(fis);
        IOUtil.closeQuietly(outStream);
	}
	//
	private void init() {
        RandomWordFactory wf = new RandomWordFactory();
        Random random=new Random();
        wf.setCharacters("abcdefghkmnpqstwxyzABCDEFGHKMNPQSTWXYZ23456789");
        wf.setMaxLength(4);
        wf.setMinLength(4);
        this.setWordFactory(wf);
        this.setHeight(40);
        this.setWidth(100);
        ConfigurableFilterFactory filterFactory = new ConfigurableFilterFactory();
        List<BufferedImageOp> filters = new ArrayList<>();
        WobbleImageOp wobbleImageOp = new WobbleImageOp();
        wobbleImageOp.setEdgeMode(AbstractImageOp.EDGE_MIRROR);
        wobbleImageOp.setxAmplitude(2.0);
        wobbleImageOp.setyAmplitude(1.0);
        filters.add(wobbleImageOp);
        filterFactory.setFilters(filters);
        this.setFilterFactory(filterFactory);
        TextRenderer textRenderer = new BestFitTextRenderer();
        textRenderer.setRightMargin(10);
        textRenderer.setLeftMargin(10);
        textRenderer.setTopMargin(10);
        textRenderer.setBottomMargin(10);
        this.setTextRenderer(textRenderer);
        RandomFontFactory fontFactory = new RandomFontFactory();
        fontFactory.setMinSize(30);
        fontFactory.setMaxSize(40);
        this.setFontFactory(fontFactory);
        this.setColorFactory(new RandomColorFactory());
        this.setBackgroundFactory(new BackgroundFactory() {
            @Override
            public void fillBackground(BufferedImage image) {
                Graphics2D graphics = image.createGraphics();
                graphics.getDeviceConfiguration().createCompatibleImage(image.getWidth(),
                        image.getHeight(), Transparency.TRANSLUCENT);
                int imgWidth = image.getWidth();
                int imgHeight = image.getHeight();
                for (int i = 0; i < 100; i++) {
                    graphics.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    int xInt = random.nextInt(imgWidth - 6);
                    int yInt = random.nextInt(imgHeight - 6);
                    graphics.fillArc(xInt, yInt, random.nextInt(6), random.nextInt(6), random.nextInt(360), random.nextInt(360));
                    if (i % 20 == 0) {
                        graphics.drawLine(xInt, yInt, random.nextInt(imgWidth), random.nextInt(imgHeight));
                    }
                }
                graphics.dispose();
            }
        });
    }
}

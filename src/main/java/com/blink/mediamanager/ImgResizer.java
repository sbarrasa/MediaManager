package com.blink.mediamanager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImgResizer {
    public static final Integer thumbnailSize = 100;
    public static final String idResizedPattern = "%s_%d";
    public Media mediaSource;
    public Map<Integer, Media> resizedMap;
    public List<Integer> widths;
    private final Integer sourceSize = -1;
    private boolean includeSource = true;

    public ImgResizer(Media mediaSource) {
       this(mediaSource, List.of(thumbnailSize));
    }

    public ImgResizer(Media mediaSource, List<Integer> widths) {
    	this.mediaSource = mediaSource;
        
        setWidths(widths);
    }

    public List<Integer> getWidths() {
        return widths;
    }

    public ImgResizer setWidths(List<Integer> widths) {
        this.widths = widths;
        this.resizedMap = null;
        return this;
    }

    public Media getThumbnail() {
        return getMap().get(thumbnailSize);
    }


    public Collection<Media> getAll() {
        return getMap().values();
    }

    public Map<Integer, Media> getMap() {
        if (resizedMap == null)
            build();

        return resizedMap;

    }

    public ImgResizer build() {
        BufferedImage image = toImg(mediaSource.getStream());
    	this.resizedMap = new HashMap<>();

        if (includeSource)
            resizedMap.put(sourceSize, mediaSource);

        widths.forEach(width -> {
            Media mediaResized = new Media();
            mediaResized.setId(String.format(idResizedPattern, mediaSource.getId(), width));
            mediaResized.setStream(toStream(resize(image, width)));

            resizedMap.put(width, mediaResized);
        });
        return this;
    }


    private BufferedImage resize(BufferedImage image, Integer width) {
        
            int height = (int) (image.getHeight() * ((double) width / image.getWidth()));

            BufferedImage resizedImage = new BufferedImage(width, height, image.getType());
            resizedImage.getGraphics().drawImage(image, 0, 0, width, height, null);
            
            return resizedImage;
            
    }


    private static InputStream toStream(BufferedImage resizedImage) {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
			ImageIO.write(resizedImage, "png", baos);
		} catch (IOException e) {
			throw new MediaError(e);
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}


	private BufferedImage toImg(InputStream sourceStream) {
       try {
    	 return ImageIO.read(sourceStream);
	   } catch (IOException e) {
	      throw new MediaError(String.format("%s is not an image", mediaSource.getId()));
	   }
	}

	public boolean isIncludeSource() {
        return includeSource;
    }

    public ImgResizer setIncludeSource(boolean includeSource) {
        this.includeSource = includeSource;
        return this;
    }
    
}

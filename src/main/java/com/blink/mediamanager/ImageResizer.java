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

public class ImageResizer {
    public Media mediaSource;
    public Map<Integer, Media> resizedMap;
    public List<Integer> widths;
    public static Integer thumbnailSize = 100;
    public static final Integer sourceSize = -1;
    public static final List<Integer> defaultSizes = List.of(sourceSize, thumbnailSize);
    public String ID_POINT = ".";
    public String ID_PATTERN = "_";
    
    public ImageResizer(Media mediaSource) {
       this(mediaSource, defaultSizes);
    }

    public ImageResizer(Media mediaSource, List<Integer> widths) {
    	this.mediaSource = mediaSource;
        
        setWidths(widths);
    }

    public List<Integer> getWidths() {
        return widths;
    }

    public ImageResizer setWidths(List<Integer> widths) {
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

    public ImageResizer build() {
        this.resizedMap = new HashMap<>();

    	try {
			BufferedImage image = toImage(mediaSource.getStream());
       
	        widths.forEach(width -> { 
	            Media mediaResized = new Media();
	            mediaResized.setId(buildId(mediaSource.getId(), width));
	
	            try {
					mediaResized.setStream(toStream(resize(image, width)));
		            resizedMap.put(width, mediaResized);
		        	
	            } catch (MediaException e) {
				}
	
	        });
		} catch (MediaException e) {
			mediaSource.setStatus( MediaStatus.err(e));
		}
    
        return this;
    }



	private static BufferedImage resize(BufferedImage image, Integer width) {
        if(width == sourceSize)
        	return image;
        
        int height = (int) (image.getHeight() * ((double) width / image.getWidth()));

        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());
        resizedImage.getGraphics().drawImage(image, 0, 0, width, height, null);
        
        return resizedImage;
            
    }


    private static InputStream toStream(BufferedImage resizedImage) throws MediaException {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
			ImageIO.write(resizedImage, "png", baos);
		} catch (IOException e) {
			throw new MediaException(e);
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}


	private BufferedImage toImage(InputStream sourceStream) throws MediaException {
       try {
    	 return ImageIO.read(sourceStream);
	   } catch (IOException e) {
	      throw new MediaException(String.format("%s is not an image", mediaSource.getId()));
	   }
	}

    private String buildId(String id, Integer width) {
    	if(width == sourceSize)
    		return id;
    	
    	int pointPos = id.indexOf(ID_POINT);
    	
    	if(pointPos <= 0)
    		return id + ID_PATTERN + width;
    	
    	return id.substring(0,pointPos ) + ID_PATTERN + width + id.substring(pointPos);

 	}
	
    
}

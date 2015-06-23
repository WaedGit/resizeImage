package org.example.web;

import org.example.service.ImageService;
import org.example.transfer.ImageResizeRequest;
import org.example.transfer.ImageResizeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class ImageController
{
  private static final String SESSION_ATTRIBUTE_NAME = "model";

  @Autowired
  ImageService imageService;

  @RequestMapping(method = RequestMethod.GET, value = "/imgScalr")
  public void imgScalr(final HttpServletResponse response, final HttpSession session) throws IOException
  {
    final ImageResizeResponse model = model(session);
    if (model != null && model.getImageScalrImage() != null)
    {
      write(model.getImageScalrImage(), response);
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/javaImageScaling")
  public void javaImageScaling(final HttpServletResponse response, final HttpSession session) throws IOException
  {
    final ImageResizeResponse model = model(session);
    if (model != null && model.getJavaImageScalingImage() != null)
    {
      write(model.getJavaImageScalingImage(), response);
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/thumbnailator")
  public void thumbnailator(final HttpServletResponse response, final HttpSession session) throws IOException
  {
    final ImageResizeResponse model = model(session);
    if (model != null && model.getThumbnailatorImage() != null)
    {
      write(model.getThumbnailatorImage(), response);
    }
  }

  @RequestMapping(method = RequestMethod.POST, value = "/")
  public String resize(final MultipartFile file, final HttpSession session) throws IOException
  {
    if (file != null)
    {
      session.setAttribute(SESSION_ATTRIBUTE_NAME, imageService.resize(new ImageResizeRequest(file.getInputStream())));
    }

    return upload();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/")
  public String upload()
  {
    return "home";
  }

  private ImageResizeResponse model(final HttpSession session)
  {
    return session.getAttribute(SESSION_ATTRIBUTE_NAME) != null
        ? (ImageResizeResponse) session.getAttribute(SESSION_ATTRIBUTE_NAME)
        : null;
  }

  private void write(final BufferedImage image, final HttpServletResponse response) throws IOException
  {
    final OutputStream stream = response.getOutputStream();
    response.setContentType("image/png");

    ImageIO.write(image, "PNG", stream);
    response.flushBuffer();
    stream.close();
  }
}

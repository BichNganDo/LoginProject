package servlets.api;

import com.google.gson.Gson;
import common.APIResult;
import entity.User;
import helper.ServletUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.JWTModel;
import model.RegisterModel;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class APIRegisterServlet extends HttpServlet {
    final static Logger logger = Logger.getLogger(APIRegisterServlet.class);

    public APIRegisterServlet() {
        PropertyConfigurator.configure("log4j.properties");

    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        APIResult result = new APIResult(0, "Success");

        String action = request.getParameter("action");
        switch (action) {

            case "getUserById": {
                int idUser = NumberUtils.toInt(request.getParameter("id_user"));

                User userByID = RegisterModel.INSTANCE.getUserRegisterByID(idUser);

                if (userByID.getId() > 0) {
                    result.setErrorCode(0);
                    result.setMessage("Thành công");
                    result.setData(userByID);
                } else {
                    result.setErrorCode(-1);
                    result.setMessage("Thất bại");
                }
                break;
            }

            default:
                throw new AssertionError();
        }

        ServletUtil.printJson(request, response, gson.toJson(result));
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        APIResult result = new APIResult(0, "Success");

        String action = request.getParameter("action");
        switch (action) {
             case "add": {
                try {
                    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
                    if (isMultipart) {
                        User user = new User();
                        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                        upload.setHeaderEncoding("UTF-8");

                        List<FileItem> items = upload.parseRequest(request);
                        for (FileItem item : items) {
                            if (item.isFormField()) {
                                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                                String fieldname = item.getFieldName();
                                String fieldvalue = item.getString("UTF-8");

                                switch (fieldname) {
                                    case "name": {
                                        user.setName(fieldvalue);
                                        break;
                                    }
                                    case "birthday": {
                                        user.setDob(fieldvalue);
                                        break;
                                    }
                                    case "favColor": {
                                        user.setFavoriteColor(fieldvalue);
                                        break;
                                    }
                                    case "email": {
                                        user.setEmail(fieldvalue);
                                        break;
                                    }
                                    case "password": {
                                        user.setPassword(fieldvalue);
                                        break;
                                    }

                                }

                            } else {
                                // Process form file field (input type="file").
                                String filename = FilenameUtils.getName(item.getName());
                                InputStream a = item.getInputStream();
                                Path uploadDir = Paths.get("avatar/" + filename);
                                Files.copy(a, uploadDir, StandardCopyOption.REPLACE_EXISTING);
                                user.setAvatar("avatar/" + filename);
                            }
                        }
                        int addUserRegister = RegisterModel.INSTANCE.addUserRegister(user.getName(), 
                        user.getEmail(), user.getPassword(), user.getFavoriteColor(), user.getDob(), user.getAvatar());

                        if (addUserRegister >= 0) {
                            result.setErrorCode(0);
                            result.setMessage("Đăng ký thành công!");
                        } else {
                            result.setErrorCode(-1);
                            result.setMessage("Đăng ký thất bại!");
                        }
                    } else {
                        result.setErrorCode(-4);
                        result.setMessage("Có lỗi");
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                break;
            }
        case "edit": {
                try {
                    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
                    if (isMultipart) {
                        User user = new User();
                        String oldAvatar = "";
                        String newAvatar = "";
                        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                        upload.setHeaderEncoding("UTF-8");

                        List<FileItem> items = upload.parseRequest(request);
                        for (FileItem item : items) {
                            if (item.isFormField()) {
                                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                                String fieldname = item.getFieldName();
                                String fieldvalue = item.getString("UTF-8");
                                switch (fieldname) {
                                    case "id": {
                                        user.setId(NumberUtils.toInt(fieldvalue));
                                        break;
                                    }
                                     case "name": {
                                        user.setName(fieldvalue);
                                        break;
                                    }
                                    case "birthday": {
                                        user.setDob(fieldvalue);
                                        break;
                                    }
                                    case "favColor": {
                                        user.setFavoriteColor(fieldvalue);
                                        break;
                                    }
                                    case "email": {
                                        user.setEmail(fieldvalue);
                                        break;
                                    }
                                    case "password": {
                                        user.setPassword(fieldvalue);
                                        break;
                                    }
                                    case "oldAvatar": {
                                        oldAvatar = fieldvalue;
                                        break;
                                    }
                                }

                            } else {
                                // Process form file field (input type="file").
                                String filename = FilenameUtils.getName(item.getName());
                                InputStream a = item.getInputStream();
                                Path uploadDir = Paths.get("/home/ngan/Code/LoginProject/avatar/" + filename);
                                Files.copy(a, uploadDir, StandardCopyOption.REPLACE_EXISTING);
                                newAvatar = "avatar/" + filename;
                            }
                        }

                        if (StringUtils.isNotEmpty(newAvatar)) {
                            user.setAvatar(newAvatar);
                        } else {
                            user.setAvatar(oldAvatar);
                        }
                        int idUser = JWTModel.INSTANCE.getIdUser(request);
                        if (idUser != user.getId()) {
                            result.setErrorCode(-1);
                            result.setMessage("Không có quyền!");
                            return;
                        }

                        User userRegisterByID = RegisterModel.INSTANCE.getUserRegisterByID(user.getId());
                        if (userRegisterByID.getId() == 0) {
                            result.setErrorCode(-1);
                            result.setMessage("Thất bại!");
                            return;
                        }

                        int editUser = RegisterModel.INSTANCE.editUserRegister(user.getId(), user.getName(), user.getEmail(), 
                        user.getPassword(), user.getFavoriteColor(), user.getDob(), user.getAvatar());

                        if (editUser >= 0) {
                            result.setErrorCode(0);
                            result.setMessage("Sửa user thành công!");
                        } else {
                            result.setErrorCode(-1);
                            result.setMessage("Sửa user thất bại!");
                        }
                    } else {
                        result.setErrorCode(-4);
                        result.setMessage("Có lỗi");
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                break;
            }
            default:
                throw new AssertionError();
        }

        ServletUtil.printJson(request, response, gson.toJson(result));

    }
}

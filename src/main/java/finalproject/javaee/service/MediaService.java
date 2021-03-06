package finalproject.javaee.service;

import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.MediaRepository;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.util.exceptions.BaseException;
import finalproject.javaee.util.exceptions.postsExceptions.InvalidPostException;
import finalproject.javaee.util.exceptions.usersExceptions.ExistException;
import finalproject.javaee.util.exceptions.usersExceptions.InvalidInputException;
import finalproject.javaee.util.exceptions.usersExceptions.NotLoggedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class MediaService {

    public static final String MEDIA_DIR = "C:\\Users\\Надежда\\Desktop\\Upload\\";
    public static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    public static final String VIDEO_PATTERN = "([^\\s]+(\\.(?i)(3gp|mp4|m4v|avi))$)";

    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private PostService postService;
    @Autowired private MediaRepository mediaRepository;

    public MessageDTO addPhoto(User user, MultipartFile image) throws IOException, InvalidInputException {
        validateMedia(image);
        formatImages(image.getOriginalFilename());
        String fileName = user.getId() + System.currentTimeMillis() + ".png";
        String mediaUri = readFile(image, fileName);
        user.setPhoto(mediaUri);
        userRepository.save(user);
        return new MessageDTO("Upload successful.");
    }

    public MessageDTO addImages(User user, MultipartFile image, long postId) throws Exception {
        validateMedia(image);
        postService.validateIfPostExist(postId);
        Post post = postRepository.findById(postId);
        if(!(post.getUserId() == user.getId())){
            throw new NotLoggedException("Cannot add media to others' posts.");
        }
        String fileName = user.getId() + System.currentTimeMillis() + ".png";
        List<Media> images = mediaRepository.findAllByMediaUrlEndingWithAndPostId(".png", postId);
        isValidNumberOfPhotos(images,image);
        String mediaUri = readFile(image, fileName);
        Media media = new Media(post.getId(), mediaUri);
        mediaRepository.save(media);
        return new MessageDTO("Upload successful.");
    }

    public MessageDTO addVideo(User user, MultipartFile video, long postId) throws Exception {
        validateMedia(video);
        postService.validateIfPostExist(postId);
        Post post = postRepository.findById(postId);
        if(!(post.getUserId() == user.getId())){
            throw new NotLoggedException("Cannot add media to others' posts.");
        }
        String fileName = user.getId() + System.currentTimeMillis() + ".mp4";
        List<Media> findVideo = mediaRepository.findAllByMediaUrlEndingWithAndPostId(".mp4", postId);
        isValidNumberOfVideo(findVideo,video);
        String videoUri = readFile(video, fileName);
        Media media = new Media(post.getId(), videoUri);
        mediaRepository.save(media);
        return new MessageDTO("Upload video successful.");
    }

    public byte[] imagesDownload(String mediaName) throws Exception {
        if (mediaRepository.existsByMediaUrl(mediaName)) {
            return download(mediaName);
        }
        if (userRepository.existsByPhoto(mediaName)) {
            return download(mediaName);
        }
        throw new ExistException("Image does not exist.");
    }

    public byte[] videoDownload(String mediaName) throws Exception {
        if (!mediaRepository.existsByMediaUrl(mediaName)) {
            throw new ExistException("Video does not exist.");
        }
        return download(mediaName);
    }

    private String readFile(MultipartFile media, String fileName) throws IOException {
        byte[] base64 = Base64.getEncoder().encode(media.getBytes());
        byte[] bytes = Base64.getDecoder().decode(base64);
        File mediaDir = new File(MEDIA_DIR);
        if(!mediaDir.exists()){
            mediaDir.mkdirs();
        }
        File newImage = new File(MEDIA_DIR + fileName);
        FileOutputStream fos = new FileOutputStream(newImage);
        fos.write(bytes);
        fos.close();
        return fileName;
    }

    private byte[] download(String mediaName) throws IOException {
        File file = new File(MEDIA_DIR + mediaName);
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fis.read(bytes);
        fis.close();
        return bytes;
    }

    public MessageDTO deletePhoto(User user){
        user.setPhoto("default.png");
        userRepository.save(user);
        return new MessageDTO("Remove photo successful.");
    }

    public MessageDTO deleteMedia(User user, long postId, long mediaId) throws BaseException{
        if(!postRepository.existsByIdAndUserId(postId,user.getId())) {
            throw new ExistException("Post does not exist.");
        }
        if(!mediaRepository.existsByIdAndPostId(mediaId, postId)){
            throw new ExistException("Image does not exist");
        }
        mediaRepository.deleteById(mediaId);
        return new MessageDTO("Delete media successful.");
    }

    public void validateMedia(MultipartFile media) throws MaxUploadSizeExceededException {
        if (media.isEmpty()){
            throw new MultipartException("Invalid media size.");
        }
        if((media.getSize() > 50000000)) {
            throw new MaxUploadSizeExceededException(50000000);
        }
    }

    private boolean isValidNumberOfPhotos(List<Media> photos,MultipartFile media) throws InvalidPostException, InvalidInputException {
        formatImages(media.getOriginalFilename());
        if (photos.size() > 2 ) {
            throw new InvalidPostException("Cannot upload more than 3 photos.");
        }
        return true;
    }

    private boolean isValidNumberOfVideo(List<Media> video,MultipartFile media) throws InvalidPostException, InvalidInputException {
        formatVideo(media.getOriginalFilename());
        if(video.size() > 0 ){
            throw new InvalidPostException("Cannot upload more than 1 video.");
        }
        return true;
    }

    private void formatImages(String media) throws InvalidInputException {
        Pattern p = Pattern.compile(IMAGE_PATTERN);
        if(!p.matcher(media).find()){
            throw new InvalidInputException("Invalid images upload.");
        }
    }

    private void formatVideo(String media) throws InvalidInputException {
        Pattern p = Pattern.compile(VIDEO_PATTERN);
        if(!p.matcher(media).find()){
            throw new InvalidInputException("Invalid images upload.");
        }
    }
}

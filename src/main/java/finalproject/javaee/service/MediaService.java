package finalproject.javaee.service;

import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.dto.pojoDTO.MediaDTO;
import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.MediaRepository;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.util.exceptions.BaseException;
import finalproject.javaee.util.exceptions.postsExceptions.InvalidPostException;
import finalproject.javaee.util.exceptions.usersExceptions.ExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@Transactional(rollbackOn = BaseException.class)
public class MediaService {

    public static final String MEDIA_DIR = "C:\\Users\\Надежда\\Desktop\\Upload\\";

    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private PostService postService;
    @Autowired private MediaRepository mediaRepository;

    public MessageDTO addPhoto(User user, MediaDTO dto) throws IOException {
        String fileName = user.getId() + System.currentTimeMillis() + ".png";
        String mediaUri = readFile(dto.getMediaUri(),fileName);
        user.setPhoto(mediaUri);
        userRepository.save(user);
        return new MessageDTO("Upload successful.");
    }

    public MessageDTO addImages(User user, MediaDTO dto, long postId) throws  Exception {
        postService.validateIfPostExist(postId);
        Post post = postRepository.findById(postId);
        List<Media> images = mediaRepository.findAllByPostId(postId);
        isValidNumberOfPhotos(images);
        String fileName = user.getId() + System.currentTimeMillis() + ".png";
        String mediaUri = readFile(dto.getMediaUri(),fileName);
        dto.setMediaUri(mediaUri);
        Media media = new Media(post.getId(), dto.getMediaUri());
        mediaRepository.save(media);
        return new MessageDTO("Upload successful.");
    }

    public MessageDTO addVideo(User user, MediaDTO dto, long postId) throws Exception{
        postService.validateIfPostExist(postId);
        Post post = postRepository.findById(postId);
        String fileName = user.getId() + System.currentTimeMillis() + ".mp4";
        List<Media> video = mediaRepository.findAllByPostId(postId);
        isValidNumberOfVideo(video,dto);
        String videoUri = readFile(dto.getMediaUri(),fileName);
        dto.setMediaUri(videoUri);
        Media media = new Media(post.getId(), dto.getMediaUri());
        mediaRepository.save(media);
        return new MessageDTO("Upload video successful.");
    }

    public byte[] imagesDownload(String mediaName) throws Exception{
        if(!mediaRepository.existsByMediaUrl(mediaName)) {
            throw new ExistException("Image does not exist.");
        }
        return download(mediaName);
    }

    public byte[] videoDownload(String mediaName) throws Exception{
        if(!mediaRepository.existsByMediaUrl(mediaName)){
            throw new ExistException("Video does not exist.");
        }
        return download(mediaName);
    }

    private String readFile(String media,String fileName) throws IOException {
        String base64 = media;
        byte[] bytes = Base64.getDecoder().decode(base64);
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

    private boolean isValidNumberOfPhotos(List<Media> photos) throws InvalidPostException {
        if(photos.size() > 3){
            throw new InvalidPostException("Cannot upload more than 3 photos.");
        }
        return true;
    }

    private boolean isValidNumberOfVideo(List<Media> video, MediaDTO media) throws InvalidPostException {
        if(video.size() > 1 ||
                media.getMediaUri().substring(media.getMediaUri().length()-4, media.getMediaUri().length()-1).equals(".mp4")){
            throw new InvalidPostException("Cannot upload more than 1 video.");
        }
        return true;
    }
}

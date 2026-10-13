package com.example.tsumugu.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.tsumugu.entity.Photo;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.exception.BadRequestException;
import com.example.tsumugu.exception.ForbiddenException;
import com.example.tsumugu.exception.ResourceNotFoundException;
import com.example.tsumugu.repository.PhotoRepository;

@Service
public class PhotoService {
	
	private static final Logger log = LoggerFactory.getLogger(PhotoService.class);

	private static final int MAX_PHOTOS_PER_POST = 4;
	
	private final PhotoRepository photoRepository;
	private final Cloudinary cloudinary;
	private final CurrentUserService currentUserService;
	
	public PhotoService(PhotoRepository photoRepository, Cloudinary cloudinary, CurrentUserService currentUserService) {
		this.photoRepository = photoRepository;
		this.cloudinary = cloudinary;
		this.currentUserService = currentUserService;
	}
	
	public Photo uploadPhoto(Post post, MultipartFile file, String comment) throws IOException {
		
		if (comment != null && comment.length() > 255) {
			throw new BadRequestException("写真のコメントは255文字以内で入力してください");
		}
		
		if (file.isEmpty()) {
			throw new BadRequestException("ファイルを選択してください");
		}
		
		List<Photo> existingPhotos = photoRepository.findByPost(post);		
		if (existingPhotos.size() >= MAX_PHOTOS_PER_POST) {
			throw new BadRequestException("写真は1投稿につき最大" + MAX_PHOTOS_PER_POST + "枚までです");
		}
		
		Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
		String imageUrl = (String) uploadResult.get("secure_url");
		
		Photo photo = new Photo();
		photo.setPost(post);
		photo.setImagePath(imageUrl);
		photo.setComment(comment);
		
		LocalDateTime now = LocalDateTime.now();
		photo.setCreatedAt(now);
		photo.setUpdatedAt(now);
		
		return photoRepository.save(photo);
	}
	
	// 投稿に紐づく写真を、追加された順に取得する
	public List<Photo> getPhotos(Post post) {
		return photoRepository.findByPostOrderByCreatedAtAsc(post);
	}
	
	// 写真のコメントを変更する(投稿の持ち主のみ)。空ならコメントを消す
	public Photo updateComment(Long photoId, String comment) {
		Photo photo = findOwnPhoto(photoId);
		
		// 空文字や空白だけのときは、コメントなし(null)として保存する
		String normalized = (comment == null || comment.isBlank()) ? null : comment;
		
		photo.setComment(normalized);
		photo.setUpdatedAt(LocalDateTime.now());
		return photoRepository.save(photo);
	}
	
	// 自分の投稿の写真を取得する(他人の写真は操作できない)
	private Photo findOwnPhoto(Long photoId) {
		Photo photo = photoRepository.findById(photoId)
				.orElseThrow(() -> new ResourceNotFoundException("写真が見つかりません"));
		
		User me = currentUserService.getCurrentUser();
		if (!photo.getPost().getUser().getId().equals(me.getId())) {
			throw new ForbiddenException("自分の投稿の写真のみ操作できます");
		}
		return photo;
	}
	
	// 写真を削除する(投稿の持ち主のみ)。DBの行と、Cloudinary上の画像の両方を消す
	public void deletePhoto(Long photoId) {
		Photo photo = findOwnPhoto(photoId);
		photoRepository.delete(photo);
		deleteFromCloudinary(photo.getImagePath());
	}

	// Cloudinary上の画像を削除する(失敗しても、写真の削除自体は止めない)
	private void deleteFromCloudinary(String imageUrl) {
		try {
			String publicId = extractPublicId(imageUrl);
			Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("invalidate", true));

			if (!"ok".equals(result.get("result"))) {
				log.warn("Cloudinary上の画像を削除できませんでした: {} (結果: {})", imageUrl, result.get("result"));
			}
		} catch (Exception e) {
			log.warn("Cloudinary上の画像の削除に失敗しました: {}", imageUrl, e);
		}
	}

	// CloudinaryのURLから、画像のID(public_id)を取り出す
	// 例:https://res.cloudinary.com/xxx/image/upload/v1790143076/abcde12345.png → abcde12345
	// フォルダ分けをしていない、今の保存のしかたを前提にしている
	private String extractPublicId(String imageUrl) {
		String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
		int dotIndex = fileName.lastIndexOf('.');
		return dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
	}
}

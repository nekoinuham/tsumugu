package com.example.tsumugu.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.tsumugu.entity.Photo;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.repository.PhotoRepository;

@Service
public class PhotoService {

	private static final int MAX_PHOTOS_PER_POST = 4;
	
	private final PhotoRepository photoRepository;
	private final Cloudinary cloudinary;
	
	public PhotoService(PhotoRepository photoRepository, Cloudinary cloudinary) {
		this.photoRepository = photoRepository;
		this.cloudinary = cloudinary;
	}
	
	public Photo uploadPhoto(Post post, MultipartFile file, String comment) throws IOException {
		List<Photo> existringPhotos = photoRepository.findByPost(post);
		if (existringPhotos.size() >= MAX_PHOTOS_PER_POST) {
			throw new IllegalStateException("写真は1投稿にすき最大" + MAX_PHOTOS_PER_POST + "枚までです");
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
}

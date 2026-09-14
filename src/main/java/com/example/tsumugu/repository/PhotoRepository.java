package com.example.tsumugu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tsumugu.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

}
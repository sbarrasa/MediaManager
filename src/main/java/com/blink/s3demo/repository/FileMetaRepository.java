package com.blink.s3demo.repository;

import com.blink.s3demo.model.FileMeta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetaRepository extends CrudRepository<FileMeta, Integer> {
}

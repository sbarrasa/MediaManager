package com.blink.s3api.repository;

import com.blink.s3api.model.FileMeta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetaRepository extends CrudRepository<FileMeta, String> {
}

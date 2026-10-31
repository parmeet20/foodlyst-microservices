package com.service.grabOrderSerice.repo;

import com.service.grabOrderSerice.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepo extends JpaRepository<Rating,String> {
}

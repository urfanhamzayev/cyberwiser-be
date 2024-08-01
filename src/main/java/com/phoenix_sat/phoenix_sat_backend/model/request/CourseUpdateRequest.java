package com.phoenix_sat.phoenix_sat_backend.model.request;

public record CourseUpdateRequest(String courseId,
                                  String name,
                                  String pictureUrl,
                                  String tags,
                                  String title,
                                  String description,
                                  String instructor,
                                  String duration,
                                  Integer availablePoint) {
}

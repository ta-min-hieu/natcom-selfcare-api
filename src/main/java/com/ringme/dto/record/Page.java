package com.ringme.dto.record;

import java.util.List;

public record Page<T>(List<T> data, int totalRecord) {
}

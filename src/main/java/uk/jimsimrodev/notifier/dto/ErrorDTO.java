package uk.jimsimrodev.notifier.dto;

import java.util.List;

public record ErrorDTO(String description, List<String> reasons) {

}

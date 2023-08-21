package com.example.doplombackend.service;

import com.example.doplombackend.exceptions.FileManageError;
import com.example.doplombackend.model.clientCustomResponses.FileEntryView;
import com.example.doplombackend.model.clientCustomResponses.NewFileNameUpload;
import com.example.doplombackend.model.storage.FileEntry;
import com.example.doplombackend.repository.FileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Transactional
    public void addFile(MultipartFile newFile) {
        try {
            fileRepository.save(FileEntry.builder()
                    .fileName(newFile.getOriginalFilename())
                    .fileSize(newFile.getSize())
                    .fileData(newFile.getBytes())
                    .build());
        } catch (IOException e) {
            throw new FileManageError(e.getMessage());
        }
    }

    @Transactional
    public byte[] getFile(String fileName) {
        Optional<FileEntry> file = fileRepository.findByFileNameEquals(fileName);
        if (file.isEmpty()) throw new FileManageError("File not found!");
        return file.get().getFileData();
    }

    @Transactional
    public void removeFile(String fileName) {
        Optional<FileEntry> file = fileRepository.findByFileNameEquals(fileName);
        if (file.isEmpty()) throw new FileManageError("File not found!");
        fileRepository.deleteById(file.get().getId());
    }

    @Transactional
    public void updateFile(String fileName, NewFileNameUpload newName) {
        Optional<FileEntry> existingFile = fileRepository.findByFileNameEquals(fileName);
        if (existingFile.isEmpty()) throw new FileManageError("File not found!");
        fileRepository.updateFile(newName.name(), existingFile.get().getId());
    }

    @Transactional
    public List<FileEntryView> getFiles(int limit) {
        List<FileEntry> filesList = fileRepository.findAll();
        List<FileEntryView> filesForThisPage = new ArrayList<>();
        for (int i = 0; i < filesList.size(); i++) {
            if (i == limit) break;
            FileEntry file = filesList.get(i);
            filesForThisPage.add(new FileEntryView(file.getFileName(), file.getFileSize()));
        }
        return filesForThisPage;
    }
}

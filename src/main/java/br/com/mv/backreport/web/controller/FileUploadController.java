package br.com.mv.backreport.web.controller;

import br.com.mv.backreport.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Autowired
    private StorageService storageService;

    @PostMapping
    public RedirectView handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam String nome, RedirectAttributes redirectAttributes) {
        storageService.store(file);

        redirectAttributes.addFlashAttribute("message", "Upload do report " + nome + " realizado com sucesso!");

        return new RedirectView("/backreport");
    }

}

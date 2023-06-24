package com.bnpp.dsibcef.apim.alias.web;

import com.bnpp.dsibcef.apim.alias.domain.entity.Alias;
import com.bnpp.dsibcef.apim.alias.domain.service.AliasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/als/v2")
public class AliasRestService {

    private AliasService aliasService;
    @Autowired
    public AliasRestService(AliasService aliasService) {
        this.aliasService=aliasService;
    }

    @GetMapping("/alias")
    public ResponseEntity<Alias> getAlias() {
       return new ResponseEntity<Alias>(aliasService.getOrCreateAliasFromPartnerAndUserId("1234","12345"),HttpStatus.OK);
    }

}

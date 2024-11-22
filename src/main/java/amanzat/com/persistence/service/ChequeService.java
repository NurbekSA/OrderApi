package amanzat.com.persistence.service;

import lombok.RequiredArgsConstructor;
import amanzat.com.persistence.model.dto.ChequeDTO;
import amanzat.com.persistence.model.entity.ChequeModel;
import amanzat.com.exception.ResourceNotFoundException;
import amanzat.com.persistence.repository.ChequeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChequeService {

    private final ChequeRepo chequeRepo;
    private static final Logger logger = LoggerFactory.getLogger(ChequeService.class);

    // Преобразование из ChequeModel в ChequeDTO
    private ChequeDTO convertToDto(ChequeModel chequeModel) {
        logger.debug("CONVERT_TO_DTO: Конвертация ChequeModel в ChequeDTO. ID: {}", chequeModel.getId());
        return new ChequeDTO(chequeModel.getId(), chequeModel.getOrderId(), chequeModel.getBody());
    }

    // Преобразование из ChequeDTO в ChequeModel
    private ChequeModel convertToEntity(ChequeDTO chequeDTO) {
        logger.debug("CONVERT_TO_ENTITY: Конвертация ChequeDTO в ChequeModel. ID: {}", chequeDTO.getId());
        return new ChequeModel(chequeDTO.getId(), chequeDTO.getOrderId(), chequeDTO.getBody());
    }

    public ChequeDTO getByOrderId(String id) {
        logger.info("GET_BY_ORDER_ID: Получение чека по Order ID: {}", id);
        ChequeModel chequeModel = chequeRepo.findByOrderId(id)
                .orElseThrow(() -> new ResourceNotFoundException("GET_BY_ORDER_ID: Чек не найден. Order ID: " + id));
        logger.info("GET_BY_ORDER_ID: Чек найден. ID: {}", chequeModel.getId());
        return convertToDto(chequeModel);
    }

    public ChequeDTO create(ChequeDTO chequeDTO) {
        logger.info("CREATE: Создание нового чека. Order ID: {}", chequeDTO.getOrderId());
        ChequeModel chequeModel = convertToEntity(chequeDTO);

        ChequeModel savedCheque = chequeRepo.save(chequeModel);
        if (savedCheque == null) {
            logger.error("CREATE: Не удалось создать чек. Order ID: {}", chequeDTO.getOrderId());
            throw new ResourceNotFoundException("CREATE: Не удалось создать чек.");
        }

        logger.info("CREATE: Чек успешно создан. ID: {}", savedCheque.getId());
        return convertToDto(savedCheque);
    }
}

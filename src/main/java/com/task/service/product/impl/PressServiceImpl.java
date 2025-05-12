package com.task.service.product.impl;

import com.task.dto.productRequest.PressRequest;
import com.task.dto.response.PressResponse;
import com.task.entities.product.Press;
import com.task.exception.BadRequestException;
import com.task.exception.ResourceNotExistsException;
import com.task.mapper.PressMapper;
import com.task.repository.product.PlateRepository;
import com.task.repository.product.PressRepository;
import com.task.service.product.PressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PressServiceImpl implements PressService {

    private final PressRepository pressRepository;
    private final PlateRepository plateRepository;
    private final PressMapper pressMapper;

    @Override
    public PressResponse addPress(PressRequest pressRequest) {
        boolean isExistPress = pressRepository.existsByPressSize(pressRequest.getPressSize());

        if (isExistPress) {
            throw new ResourceNotExistsException("Press size is already exists");
        }

        Press press = pressRepository.save(pressMapper.reqToEntity(pressRequest));
        return pressMapper.entityToRes(press);
    }

    @Override
    public List<PressResponse> getAll() {
        List<Press> pressList = pressRepository.findAll();
        return pressMapper.entityToRes(pressList);
    }

    @Override
    public PressResponse updatePress(Long pressId, PressRequest pressRequest) {
        Press press = pressRepository.findById(pressId)
                .orElseThrow(() -> new ResourceNotExistsException("Press id : " + pressId + " is not found !!"));

        press.setPressSize((pressRequest.getPressSize() == null) ? press.getPressSize() : pressRequest.getPressSize());
        press.setMaxChamber((pressRequest.getMaxChamber() == null) ? press.getMaxChamber() : pressRequest.getMaxChamber());
        press.setCakeAirT((pressRequest.getCakeAirT() == null) ? press.getCakeAirT() : pressRequest.getCakeAirT());
        press.setCyFwdT((pressRequest.getCyFwdT() == null) ? press.getCyFwdT() : pressRequest.getCyFwdT());
        press.setCyRevT((pressRequest.getCyRevT() == null) ? press.getCyRevT() : pressRequest.getCyRevT());
        press.setDtAvailable((pressRequest.getDtAvailable() == null) ? press.getDtAvailable() : pressRequest.getDtAvailable());
        press.setDtOpenT((pressRequest.getDtOpenT() == null) ? press.getDtOpenT() : pressRequest.getDtOpenT());
        press.setDtClosedT((pressRequest.getDtClosedT() == null) ? press.getDtClosedT() : pressRequest.getDtClosedT());
        press.setPsAvailable((pressRequest.getPsAvailable() == null) ? press.getPsAvailable() : pressRequest.getPsAvailable());
        press.setPsFwdFPlateT((pressRequest.getPsFwdFPlateT() == null) ? press.getPsFwdFPlateT() : pressRequest.getPsFwdFPlateT());
        press.setPsFwdT((pressRequest.getPsFwdT() == null) ? press.getPsFwdT() : pressRequest.getPsFwdT());
        press.setPsFwdDT((pressRequest.getPsFwdDT() == null) ? press.getPsFwdDT() : pressRequest.getPsFwdDT());
        press.setPsRevT((pressRequest.getPsRevT() == null) ? press.getPsRevT() : pressRequest.getPsRevT());
        press.setPsRevDT((pressRequest.getPsRevDT() == null) ? press.getPsRevDT() : pressRequest.getPsRevDT());
        press.setCwAvailable((pressRequest.getCwAvailable() == null) ? press.getCwAvailable() : pressRequest.getCwAvailable());
        press.setCwFwdT((pressRequest.getCwFwdT() == null) ? press.getCwFwdT() : pressRequest.getCwFwdT());
        press.setCwFwdDT((pressRequest.getCwFwdDT() == null) ? press.getCwFwdDT() : pressRequest.getCwFwdDT());
        press.setCwRevT((pressRequest.getCwRevT() == null) ? press.getCwRevT() : pressRequest.getCwRevT());
        press.setCwRevDT((pressRequest.getCwRevDT() == null) ? press.getCwRevDT() : pressRequest.getCwRevDT());
        press.setCwDownT((pressRequest.getCwDownT() == null) ? press.getCwDownT() : pressRequest.getCwDownT());
        press.setCwDownDT((pressRequest.getCwDownDT() == null) ? press.getCwDownDT() : pressRequest.getCwDownDT());
        press.setCwUpT((pressRequest.getCwUpT() == null) ? press.getCwUpT() : pressRequest.getCwUpT());
        press.setCwUpDT((pressRequest.getCwUpDT() == null) ? press.getCwUpDT() : pressRequest.getCwUpDT());
        press.setCwFlowRate((pressRequest.getCwFlowRate() == null) ? press.getCwFlowRate() : pressRequest.getCwFlowRate());

        Press updatePress = pressRepository.save(press);

        return pressMapper.entityToRes(updatePress) ;
    }

    @Override
    public void deletePressById(Long pressId) {
        boolean exists = pressRepository.existsById(pressId);
        if (!exists) {
            throw new BadRequestException("Press with press size " + pressId + " does not exist.");
        }
        pressRepository.deleteById(pressId);
    }

    @Override
    public void deleteAllPress() {
        List<Press> press = pressRepository.findAll();

        if (press.isEmpty()) {
            throw new BadRequestException("Press is empty!!");
        }
        pressRepository.deleteAll();
    }
}

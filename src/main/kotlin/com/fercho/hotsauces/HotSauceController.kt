package com.fercho.hotsauces

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Optional

@RestController
@RequestMapping("/api/hotsauces")
class HotSauceController(private val hotSauceRepository: HotSauceRepository) {

    @RequestMapping("")
    fun getAll(
        @RequestParam(value = "brandname", required = false, defaultValue = "") brandNameFilter: String,
        @RequestParam(value = "saucename", required = false, defaultValue = "") sauceNameFilter: String,
        @RequestParam(value = "desc", required = false, defaultValue = "") descFilter: String,
        @RequestParam(value="minheat", required = false, defaultValue = "") minHeat: String,
        @RequestParam(value="maxheat", required = false, defaultValue = "") maxHeat: String
    ): ResponseEntity<List<HotSauce>> {
        val MAX_SCOVILLE = 3_000_000

        val minHeatFilter = if (!minHeat.isNullOrBlank()) minHeat.toInt() else 0
        val maxHeatFilter = if (!maxHeat.isNullOrBlank()) maxHeat.toInt() else MAX_SCOVILLE

        return ResponseEntity(
            hotSauceRepository.findAll().
                    filter { it.brandName.contains(brandNameFilter, true) }.
                    filter { it.sauceName.contains(sauceNameFilter, true) }.
                    filter { it.description.contains(descFilter, true) }.
                    filter { it.heat >= minHeatFilter }.
                    filter { it.heat <= maxHeatFilter },
            HttpStatus.OK
        )
    }

    @GetMapping("/count")
    fun getCount():ResponseEntity<Long> = ResponseEntity(hotSauceRepository.count(), HttpStatus.OK)

    @GetMapping("/{id}")
    fun getHotSauce(@PathVariable id: Long) : ResponseEntity<Optional<HotSauce>>{
        if (hotSauceRepository.existsById(id)){
            return ResponseEntity(hotSauceRepository.findById(id), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping()
    fun createHotSauce(@RequestBody hotSauce: HotSauce): ResponseEntity<HotSauce>{
        return ResponseEntity(hotSauceRepository.save(hotSauce), HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateHotSauce(@PathVariable id: Long, @RequestBody sauceChanges: HotSauce): ResponseEntity<HotSauce?>{
        if (hotSauceRepository.existsById(id)){
            val originalSource = hotSauceRepository.findById(id).get()
            val updateSource = HotSauce(
                id = id,
                brandName = if (sauceChanges.brandName != "") sauceChanges.brandName else originalSource.brandName,
                sauceName = if (sauceChanges.sauceName != "") sauceChanges.sauceName else originalSource.sauceName,
                description = if (sauceChanges.description != "") sauceChanges.description else originalSource.description,
                url = if (sauceChanges.url != "") sauceChanges.url else originalSource.url,
                heat = if (sauceChanges.heat != 0) sauceChanges.heat else originalSource.heat
            )
            return ResponseEntity(hotSauceRepository.save(updateSource), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/{id}")
    fun deleteHotSauce(id: Long): ResponseEntity<HotSauce?>{
        if (hotSauceRepository.existsById(id)){
            hotSauceRepository.deleteById(id)
            return ResponseEntity(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }
}
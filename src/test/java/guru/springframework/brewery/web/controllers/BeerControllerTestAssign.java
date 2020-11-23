package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerPagedList;
import guru.springframework.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTestAssign {

    @Mock
    private BeerService beerService;

    @InjectMocks
    private BeerController beerController;

    private BeerDto beerDto;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        beerDto = BeerDto.builder().id(UUID.randomUUID())
                .version(1)
                .beerName("Beer1")
                .beerStyle(BeerStyleEnum.PALE_ALE)
                .price(new BigDecimal("12.99"))
                .quantityOnHand(4)
                .upc(123456789012L)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(beerController).build();
    }

    @Test
    void getGeerByIdTest() throws Exception {

        given(beerService.findBeerById(beerDto.getId())).willReturn(beerDto);
        mockMvc.perform(get("/api/v1/beer/" + beerDto.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(beerDto.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(beerDto.getBeerName())));
    }

    @DisplayName("List Ops --")
    @Nested
    public class testListOps{

        @Captor
        ArgumentCaptor<String> beerNameCaptor;

        @Captor
        ArgumentCaptor<BeerStyleEnum> beerStyleEnumCaptor;

        @Captor
        ArgumentCaptor<PageRequest> pageRequestCaptor;

        BeerPagedList beerPagedList;

        @BeforeEach
        void setUp() {
            List<BeerDto> beers = new ArrayList();
            beers.add(beerDto);
            beers.add(BeerDto.builder().id(UUID.randomUUID())
                    .version(1)
                    .beerName("Beer2")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .price(new BigDecimal("15.99"))
                    .quantityOnHand(4)
                    .upc(123456789012L)
                    .createdDate(OffsetDateTime.now())
                    .lastModifiedDate(OffsetDateTime.now())
                    .build());

            beerPagedList = new BeerPagedList(beers, PageRequest.of(1, 1), 2L);

            given(beerService.listBeers(beerNameCaptor.capture(), beerStyleEnumCaptor.capture(),
                    pageRequestCaptor.capture())).willReturn(beerPagedList);
        }

        @DisplayName("Test list beer - no parameter")
        @Test
        void testListBeers() throws Exception {
            mockMvc.perform(get("/api/v1/beer"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.content",hasSize(2)))
                    .andExpect(jsonPath("$.content[1].beerName", is("Beer2")));
        }
    }
}

package dev.ingestion.web.rest;

import static dev.ingestion.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.ingestion.IngestionApiApp;
import dev.ingestion.domain.Status;
import dev.ingestion.repository.StatusRepository;
import dev.ingestion.service.StatusService;
import dev.ingestion.service.dto.StatusDTO;
import dev.ingestion.service.mapper.StatusMapper;
import dev.ingestion.web.rest.errors.ExceptionTranslator;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

/**
 * Integration tests for the {@link StatusResource} REST controller.
 */
@SpringBootTest(classes = IngestionApiApp.class)
public class StatusResourceIT {

    private static final Long DEFAULT_STATUS_ID = 1L;
    private static final Long UPDATED_STATUS_ID = 2L;
    private static final Long SMALLER_STATUS_ID = 1L - 1L;

    private static final String DEFAULT_STATUS_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STATUS_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS_NAME = "AAAAAAAAAA";
    private static final String UPDATED_STATUS_NAME = "BBBBBBBBBB";

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private StatusMapper statusMapper;

    @Autowired
    private StatusService statusService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restStatusMockMvc;

    private Status status;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final StatusResource statusResource = new StatusResource(statusService);
        this.restStatusMockMvc = MockMvcBuilders.standaloneSetup(statusResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator)
            .build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Status createEntity(EntityManager em) {
        Status status = new Status().statusId(DEFAULT_STATUS_ID).statusCode(DEFAULT_STATUS_CODE).statusName(DEFAULT_STATUS_NAME);
        return status;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Status createUpdatedEntity(EntityManager em) {
        Status status = new Status().statusId(UPDATED_STATUS_ID).statusCode(UPDATED_STATUS_CODE).statusName(UPDATED_STATUS_NAME);
        return status;
    }

    @BeforeEach
    public void initTest() {
        status = createEntity(em);
    }

    @Test
    @Transactional
    public void createStatus() throws Exception {
        int databaseSizeBeforeCreate = statusRepository.findAll().size();

        // Create the Status
        StatusDTO statusDTO = statusMapper.toDto(status);
        restStatusMockMvc
            .perform(
                post("/api/statuses").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(statusDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeCreate + 1);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getStatusId()).isEqualTo(DEFAULT_STATUS_ID);
        assertThat(testStatus.getStatusCode()).isEqualTo(DEFAULT_STATUS_CODE);
        assertThat(testStatus.getStatusName()).isEqualTo(DEFAULT_STATUS_NAME);
    }

    @Test
    @Transactional
    public void createStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = statusRepository.findAll().size();

        // Create the Status with an existing ID
        status.setId(1L);
        StatusDTO statusDTO = statusMapper.toDto(status);

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatusMockMvc
            .perform(
                post("/api/statuses").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(statusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkStatusIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = statusRepository.findAll().size();
        // set the field null
        status.setStatusId(null);

        // Create the Status, which fails.
        StatusDTO statusDTO = statusMapper.toDto(status);

        restStatusMockMvc
            .perform(
                post("/api/statuses").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(statusDTO))
            )
            .andExpect(status().isBadRequest());

        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = statusRepository.findAll().size();
        // set the field null
        status.setStatusCode(null);

        // Create the Status, which fails.
        StatusDTO statusDTO = statusMapper.toDto(status);

        restStatusMockMvc
            .perform(
                post("/api/statuses").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(statusDTO))
            )
            .andExpect(status().isBadRequest());

        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = statusRepository.findAll().size();
        // set the field null
        status.setStatusName(null);

        // Create the Status, which fails.
        StatusDTO statusDTO = statusMapper.toDto(status);

        restStatusMockMvc
            .perform(
                post("/api/statuses").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(statusDTO))
            )
            .andExpect(status().isBadRequest());

        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStatuses() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList
        restStatusMockMvc
            .perform(get("/api/statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].statusId").value(hasItem(DEFAULT_STATUS_ID.intValue())))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE.toString())))
            .andExpect(jsonPath("$.[*].statusName").value(hasItem(DEFAULT_STATUS_NAME.toString())));
    }

    @Test
    @Transactional
    public void getStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get the status
        restStatusMockMvc
            .perform(get("/api/statuses/{id}", status.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(status.getId().intValue()))
            .andExpect(jsonPath("$.statusId").value(DEFAULT_STATUS_ID.intValue()))
            .andExpect(jsonPath("$.statusCode").value(DEFAULT_STATUS_CODE.toString()))
            .andExpect(jsonPath("$.statusName").value(DEFAULT_STATUS_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStatus() throws Exception {
        // Get the status
        restStatusMockMvc.perform(get("/api/statuses/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Update the status
        Status updatedStatus = statusRepository.findById(status.getId()).get();
        // Disconnect from session so that the updates on updatedStatus are not directly saved in db
        em.detach(updatedStatus);
        updatedStatus.statusId(UPDATED_STATUS_ID).statusCode(UPDATED_STATUS_CODE).statusName(UPDATED_STATUS_NAME);
        StatusDTO statusDTO = statusMapper.toDto(updatedStatus);

        restStatusMockMvc
            .perform(put("/api/statuses").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(statusDTO)))
            .andExpect(status().isOk());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getStatusId()).isEqualTo(UPDATED_STATUS_ID);
        assertThat(testStatus.getStatusCode()).isEqualTo(UPDATED_STATUS_CODE);
        assertThat(testStatus.getStatusName()).isEqualTo(UPDATED_STATUS_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Create the Status
        StatusDTO statusDTO = statusMapper.toDto(status);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(put("/api/statuses").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(statusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        int databaseSizeBeforeDelete = statusRepository.findAll().size();

        // Delete the status
        restStatusMockMvc
            .perform(delete("/api/statuses/{id}", status.getId()).accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Status.class);
        Status status1 = new Status();
        status1.setId(1L);
        Status status2 = new Status();
        status2.setId(status1.getId());
        assertThat(status1).isEqualTo(status2);
        status2.setId(2L);
        assertThat(status1).isNotEqualTo(status2);
        status1.setId(null);
        assertThat(status1).isNotEqualTo(status2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatusDTO.class);
        StatusDTO statusDTO1 = new StatusDTO();
        statusDTO1.setId(1L);
        StatusDTO statusDTO2 = new StatusDTO();
        assertThat(statusDTO1).isNotEqualTo(statusDTO2);
        statusDTO2.setId(statusDTO1.getId());
        assertThat(statusDTO1).isEqualTo(statusDTO2);
        statusDTO2.setId(2L);
        assertThat(statusDTO1).isNotEqualTo(statusDTO2);
        statusDTO1.setId(null);
        assertThat(statusDTO1).isNotEqualTo(statusDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(statusMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(statusMapper.fromId(null)).isNull();
    }
}

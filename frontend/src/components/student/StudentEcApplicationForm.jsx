import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useAuth } from '../../providers/AuthProvider'
import React, { useState } from 'react'
import { Form, Container, Row, Col, Spinner, Button, Alert } from 'react-bootstrap'
import { getModules } from '../../api/modules'
import { bytesToMb, getCurrentDate } from '../../utils'
import { createEcApplication } from '../../api/ecApplications'
import { createEvidence } from '../../api/evidence'
import { createModuleRequest } from '../../api/moduleRequests'

export default function StudentEcApplicationForm() {
  const {user} = useAuth()
  const queryClient = useQueryClient()
  const [showDateAlert, setShowDateAlert] = useState(false)
  const [showFileAlert, setShowFileAlert] = useState(false)


  // State for module requests made by the student
  const [moduleRequests, setModuleRequests] = useState(([{
    requestedOutcome: '',
    moduleCode: ''
  }]))

  // State for the rest of the form fields
  const [formData, setFormData] = useState({
    circumstancesDetails: '',
    startDate: '',
    endDate: '',
    files: []
  })


  // Get the details of all modules
  const modulesQuery = useQuery({
    queryKey: ["modules"],
    queryFn: () => getModules()
  })

  const createEcApplicationMutation = useMutation({
    mutationFn: createEcApplication,
    onSuccess: data => {
      queryClient.invalidateQueries(["ecApplication"])
    },
  })

  const createEvidenceMutation = useMutation({
    mutationFn: createEvidence,
    onSuccess: data => {
      queryClient.invalidateQueries(["evidence"])
    },
  })

  const createModuleRequestMutation = useMutation({
    mutationFn: createModuleRequest,
    onSuccess: data => {
      queryClient.invalidateQueries(["moduleRequests"])
      console.log("RESULT: ", data)
    }
  })

  if (modulesQuery.isLoading)
    return (
      <Container className='mt-3'>
        <Row>
        <Col md={{offset: 6 }}>
          <Spinner animation="border" />
        </Col>
        </Row>
      </Container>
    )
  
  if (modulesQuery.isError)
    return <h1>Error fetching modules: {modulesQuery.error.response?.status}</h1>
  
  if (createEcApplicationMutation.isError)
    return <h1>Error submitting EC application: {createEcApplicationMutation.error.response?.status}</h1>
  
  if (createEvidenceMutation.isError)
    return <h1>Error uploading evidence: {createEvidenceMutation.error.response?.status}</h1>

  if (createModuleRequestMutation.isError)
    return <h1>Error creating module requests: {createModuleRequestMutation.error.response?.status}</h1>

  const modules = modulesQuery.data


  function addModuleRequest() {
    setModuleRequests(prevRequests => [...prevRequests, {
      requestedOutcome: '',
      moduleCode: ''
    }])
  }

  function removeModuleRequest() {
    setModuleRequests(prevRequests => {
      const prevRequestsCopy = [...prevRequests]
      prevRequestsCopy.pop()
      return prevRequestsCopy
    }
  )}

  // Handle change for the module outcome request select
  const handleOutcomeRequestChange = (index, event) => {
    const newModuleRequests = [...moduleRequests];
    newModuleRequests[index].requestedOutcome = event.target.value;
    setModuleRequests(newModuleRequests)
  }

  // Handle change for the module select
  const handleModuleChange = (index, event) => {
    const newModuleRequests = [...moduleRequests];
    newModuleRequests[index].moduleCode = event.target.value;
    setModuleRequests(newModuleRequests)
  }

  // Handle change for the text area and date inputs
  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData(prevFormData => ({
      ...prevFormData,
      [name]: value
    }))
  }

  // Handle file input change
  const handleFileChange = (e) => {
    setFormData(prevFormData => ({
      ...prevFormData,
      files: e.target.files
    }))
  }

  // Handle form submission
  const handleSubmit = (event) => {
    event.preventDefault()

    console.log('Selected modules:', moduleRequests)
    console.log('rest: ', formData)

    // Validate start and end dates
    const startDate = new Date(formData.startDate);
    const endDate = new Date(formData.endDate);
    if (startDate > endDate) {
      setShowDateAlert(true)
      return;
    }

    // Validate file sizes
    Array.from(formData.files).forEach(file => {
      if (bytesToMb(file.size) > 50 )
        setShowFileAlert(true)
      return;
    })

    // Create the EC application
    createEcApplicationMutation.mutate({
      circumstancesDetails: formData.circumstancesDetails,
      affectedDateStart: formData.startDate,
      affectedDateEnd: formData.endDate,
      submittedOn: getCurrentDate(),
      studentId: user.id
    }, {
      onSuccess: data =>{
        // Upload the evidence
        Array.from(formData.files).forEach((file, index) => {
          const fData = new FormData()
          fData.append('file', file)
          createEvidenceMutation.mutate({
              formData: fData,
              ecApplicationId: data.id
          })
      })

      // Create the module requests
      moduleRequests.forEach(request => {
        createModuleRequestMutation.mutate({
          requestedOutcome: request.requestedOutcome,
          moduleCode: request.moduleCode,
          ecApplicationId: data.id
        })
      })
    }
    })
  }

  return (
    <Container className='mt-3'>
    <Row>
        <Col md={{offset: 1 }}>
          <h4 className='mb-3 text-center'>Extenuating Circumstances Application Form</h4>
        </Col>
      </Row>
      <Row>
        <Col md={{offset: 3 }}>
          <Form className='w-75' onSubmit={handleSubmit}>
            <Form.Group className='mb-5' controlId='ecForm.ControlTextArea1'>
              <Form.Label>Circumstances Details</Form.Label>
              <Form.Control as="textarea" 
                rows={5}    
                name="circumstancesDetails"
                value={formData.circumstancesDetails}
                onChange={handleChange}
                required
              />
              <Form.Text id='circumstancesHelpBlock' muted>
                Please thoroughly explain your extenuating circumstances. Details on the university's policy regarding exteunating circumstances can be
                found <a href='https://students.sheffield.ac.uk/extenuating-circumstances/policy-procedure-23-24#extenuating-circumstances-policy-and-procedure'
                target='_blank'>here. </a>
              </Form.Text>
            </Form.Group>

            <Form.Group className='mb-5' controlId='ecForm.DateArea1'>
              <Form.Label>Period Affected By Circumstances</Form.Label>
              <Row>
                <Col>
                <Form.Text id='startDateHelpBlock' muted>Start Date</Form.Text>
                </Col>
                <Col>
                <Form.Text id='startDateHelpBlock' muted>End Date</Form.Text>
                </Col>
              </Row>
              <Row>
                <Col>
                  <input type='date' 
                    name='startDate'
                    value={formData.startDate}
                    onChange={handleChange}
                    max={getCurrentDate()}
                    required
                  />
                </Col>
                <Col>
                <input
                  type='date'
                  name='endDate'
                  value={formData.endDate}
                  onChange={handleChange}
                  max={getCurrentDate()}
                  required
                />
                </Col>
              </Row>
              {showDateAlert && 
                <Alert className=' mt-2' variant="danger" onClose={() => setShowDateAlert(false)} style={{width: "20rem"}} dismissible>
                    Start date cannot be after end date
                </Alert>
              }
            </Form.Group>

            <Form.Group className='mb-5' controlId='ecForm.SelectArea1'>
              <Form.Label>Module Outcome Requests</Form.Label>
              <Row>
                <Col>
                <Form.Text id='moduleHelpBlock' muted>Module</Form.Text>
                </Col>
                <Col>
                <Form.Text id='requestHelpBlock' muted>Request</Form.Text>
                </Col>
              </Row>
              {moduleRequests.map((value, index) => 
                <Row key={index} className='mb-1'>
                  <Col>
                    <Form.Select
                    value={moduleRequests[index].requestedOutcome}
                    onChange={(event) => handleOutcomeRequestChange(index, event)}
                    required
                    >
                      <option value="">Select an option</option>
                      <option value="Deadline Extension">Deadline Extension</option>
                      <option value="Disregard Missing Component Mark">Disregard Missing Component Mark</option>
                      <option value="Remove Lateness Penalties">Remove Lateness Penalties</option>
                      <option value="Defer Formal Examination">Defer Formal Examination</option>
                      <option value="Defer Formal Assessment">Defer Formal Assessment</option>
                    </Form.Select>
                  </Col>
                  <Col>
                    <Form.Select
                      value={moduleRequests[index].moduleCode}
                      onChange={(event) => handleModuleChange(index, event)}
                      required
                    >
                      <option value="">Select a module</option>
                      {modules.map(module => 
                        <option key={module.code} value={module.code}>{module.code} {module.name}</option>
                      )}
                    </Form.Select>
                  </Col>
                </Row>
              )}
              <Button size='sm' className='mt-2 me-2' variant='info' onClick={addModuleRequest}>Add Module Request</Button>
              {moduleRequests.length >1 && 
                <Button size='sm' className='mt-2' variant='danger' onClick={removeModuleRequest}>Remove Module Request</Button>
              }
            </Form.Group>
            
            <Form.Group className='mb-3' controlId='ecForm.FileMutilple'>
                <Form.Label>Evidence</Form.Label>
                <Form.Control type='file' multiple onChange={handleFileChange} />
                <Form.Text id='filesHelpBlock' muted>Please submit your evidence here (max 50MB per file)</Form.Text>
            </Form.Group>
            {showFileAlert && 
                <Alert variant="danger" onClose={() => setShowFileAlert(false)} style={{width: "20rem"}} dismissible>
                    One of the files is larger than 50MB
                </Alert>
            }
            
            <Button variant="primary" type="submit">Submit Application</Button>
          </Form>
        </Col>
      </Row>
      </Container>
  )
}

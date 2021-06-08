# submissions-task

## Description
Application enables users to manage submissions. App provides 8 Endpoints with different functionalities:
 
### CREATE
##### PATH: `/api/create`

##### REQUEST BODY: 
`{ 
"title" : "Your title",
"content": "Your content"
}` 
               
This Endpoint allows for creating new submissions. Title names must be _**UNIQUE!**_ (Application allows Users to interact with submissions by their names, so Uniqueness is a must) Title and content must be provided. If request is successful, status of submission is set to **CREATED**      
   
### VERIFY
##### PATH: `/api/verify`

##### REQUEST BODY: 
`{ 
"title" : "Your title",
"content": "Your content"
}` 
               
This Endpoint allows for verifying submissions with **CREATED** status. This endpoint allows for change of submission's content. If no content is supplied, content is not changed. Upon successful verification, status of submission is set to **VERIFIED**      
   
### DELETE
##### PATH: `/api/delete`

##### REQUEST BODY: 
`{ 
"title" : "Your title",
"reason": "Reason why submission is deleted"
}` 
               
Not all submissions have to be verified, this Endpoint allows for deleting submissions with **CREATED** status. Reason for deleting a submission must be provided to request body. Upon successful deletion, status of submission is set to **DELETED**   
   
### REJECT
##### PATH: `/api/reject`

##### REQUEST BODY: 
`{ 
"title" : "Your title",
"reason": "Reason why submission is rejected"
}` 
               
This Endpoint allows for rejecting submissions with **VERIFIED** or **ACCEPTED** status. Reason for rejecting a submission must be provided to request body. Upon successful rejection, status of submission is set to **REJECTED**     
   
### ACCEPT
##### PATH: `/api/accept`

##### REQUEST BODY: 
`{ 
"title" : "Your title"
}` 
               
If submission has **VERIFIED** status and submission is correct, it can be accepted. Endpoint is not using RequestParams, because title may have whitespaces, and it standardizes the use of the app. Upon acceptation, status of submission is set to **ACCEPTED**     
   
### PUBLISH
##### PATH: `/api/publish`

##### REQUEST BODY: 
`{ 
"title" : "Your title"
}` 
               
If submission has **ACCEPTED** status and submission is still correct, it can be published. When submission is published, it receives generated numeric ID. Upon publishing, status of submission is set to **PUBLISHED**     
   

### CURRENT
##### PATH: `/api/current`
  
##### REQUEST PARAMETERS (optional): 
- `title`
- `status`

This endpoint returns paginated view (default 10 submissions per page) of all submissions in their current state. Filtering is done by providing request parameters. If nothing is provided, all submissions in their respectful current state will be returned. Submissions are sorted by title, then status in ascending fashion.                 
   

### HISTORY
##### PATH: `/api/history`

##### REQUEST BODY: 
`{ 
"title" : "Your title"
}` 
  

This endpoint returns history of submissions, sorted by change of state in descending order.                 
   

#### Launching Application
Starting application is simple, just use docker compose:

`docker-compose build && docker-compose up`

If there are any port clashes, change desired ones in `docker-compose.yaml` 
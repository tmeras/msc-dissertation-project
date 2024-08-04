import {useState} from 'react'
import { Link } from 'react-router-dom'

export default function ErrorPage(props) {
  return (
    <div className="text-center mt-5">
        <h2>Error</h2>
        <p className="fw-medium fs-4">{props.errorMessage}</p>
        {props.redirectTo === "/login" ? 
          <p className="fs-5">Please <Link to="/login" replace>login here</Link></p>
          :
          <p className="fs-5">Please use the navigation bar to navigate</p>
        }
    </div>
  )
}

import { Link } from 'react-router-dom'

export default function ErrorPage(props) {
    return (
        <div className="text-center mt-5">
            <h3>There was an unexpected error {!!props.errorTitle && props.errorTitle}</h3>
            <p className="fw-medium fs-4">{props.errorMessage}</p>
            {props.redirectTo === "/login" && <p className="fs-5">Please <Link to="/login" replace>login here</Link></p>
            }
            {props.redirectTo === "refresh" && <p className="fs-5">Please try again later</p>
            }
            {!!!props.redirectTo &&
                <p className="fs-5">Please use the navigation bar to navigate</p>
            }
        </div>
    )
}
